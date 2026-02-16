package br.ifba.edu.clinica.services;

import br.ifba.edu.clinica.dtos.ConsultaDTO;
import br.ifba.edu.clinica.dtos.DadosConsultaDTO;
import br.ifba.edu.clinica.entities.*;
import br.ifba.edu.clinica.repositories.ConsultaRepository;
import br.ifba.edu.clinica.repositories.MedicoRepository;
import br.ifba.edu.clinica.repositories.PacienteRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ConsultaService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    private static final LocalTime ABERTURA = LocalTime.of(7, 0);
    private static final LocalTime FECHAMENTO = LocalTime.of(19, 0);
    private static final Duration DURACAO = Duration.ofHours(1);
    private static final Duration ANTECEDENCIA_MINIMA = Duration.ofMinutes(30);
    private static final Duration ANTECEDENCIA_MINIMA_CANCELAMENTO = Duration.ofHours(24);

    public ConsultaService(
            ConsultaRepository consultaRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @Transactional
    public ConsultaDTO agendar(Long pacienteId, Long medicoId, LocalDateTime dataHora, String descricao, String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        Paciente paciente;

        if (pacienteId != null) {
            paciente = pacienteRepository.findById(pacienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
            if (!isAdmin && !paciente.getUsername().equals(currentUsername)) {
                throw new AccessDeniedException("Você não tem permissão para agendar consultas para outro paciente.");
            }
        } else {
            paciente = pacienteRepository.findByUsuarioUsername(currentUsername)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado para o usuário logado. Se você é um administrador, informe o ID do paciente."));
        }
        if (!pacienteAtivo(paciente)) {
            throw new IllegalArgumentException("Paciente inativo");
        }
        if (!antecedenciaValida(dataHora)) {
            throw new IllegalArgumentException("Agendamento deve ser feito com pelo menos 30 minutos de antecedência");
        }
        if (!horarioValido(dataHora)) {
            throw new IllegalArgumentException("Data/hora fora do horário de funcionamento da clínica");
        }
        if (temConsultaNoMesmoDia(paciente, dataHora.toLocalDate())) {
            throw new IllegalArgumentException("Paciente já possui consulta no mesmo dia");
        }

        Medico medico = null;
        if (medicoId != null) {
            medico = medicoRepository.findById(medicoId)
                    .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado"));
            if (!medicoAtivo(medico)) {
                throw new IllegalArgumentException("Médico inativo");
            }
            if (medicoOcupado(medico, dataHora)) {
                throw new IllegalArgumentException("Médico já possui consulta nesse horário");
            }
        } else {
            medico = medicoAleatorio(dataHora)
                    .orElseThrow(() -> new IllegalArgumentException("Nenhum médico disponível na data/hora informada"));
        }

        Consulta consulta = new Consulta(paciente, medico, dataHora, descricao);
        Consulta consultaSalva = consultaRepository.save(consulta);
        DadosConsultaDTO dadosConsulta = new DadosConsultaDTO(paciente.getNome(), medico.getNome(), dataHora, paciente.getUsername(), medico.getUsuario().getUsername(), "AGENDADA");
        this.rabbitTemplate.convertAndSend("email.ativacao", dadosConsulta);
        return new ConsultaDTO(consultaSalva);
    }

    @Transactional
    public ConsultaDTO concluirConsulta(Long consultaId) {
        Optional<Consulta> consultaOpt = consultaRepository.findById(consultaId);
        if (consultaOpt.get().getStatus() == Status.EM_ANDAMENTO) {
            Consulta consulta = consultaOpt.get();
            consulta.setStatus(Status.CONCLUíDA);
            consultaRepository.save(consulta);
            return new ConsultaDTO(consulta);
        } else {
            return null;
        }
    }

    @Transactional
    public ConsultaDTO alterarDataHoraConsulta(Long consultaId, LocalDateTime novaDataHora) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada"));

        Paciente paciente = consulta.getPaciente();

        // Validação de permissão
        if (!isAdmin && !paciente.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Você não tem permissão para alterar esta consulta.");
        }

        if (!pacienteAtivo(paciente)) {
            throw new IllegalArgumentException("Paciente inativo");
        }

        if (consulta.getStatus() != Status.AGENDADA) {
            throw new IllegalArgumentException("Apenas consultas agendadas podem ser reagendadas");
        }

        if (!antecedenciaValida(novaDataHora)) {
            throw new IllegalArgumentException("O reagendamento deve ser feito com pelo menos 30 minutos de antecedência");
        }

        if (!horarioValido(novaDataHora)) {
            throw new IllegalArgumentException("Data/hora fora do horário de funcionamento da clínica");
        }

        if (medicoOcupadoPorOutraConsulta(consulta.getMedico(), novaDataHora, consultaId)) {
            throw new IllegalArgumentException("Médico já possui consulta nesse horário");
        }

        consulta.setDataHora(novaDataHora);
        consultaRepository.save(consulta);
        DadosConsultaDTO dadosConsultaAlterada = new DadosConsultaDTO(paciente.getNome(), consulta.getMedico().getNome(), novaDataHora, paciente.getUsername(), consulta.getMedico().getUsuario().getUsername(), "REAGENDADA");
        this.rabbitTemplate.convertAndSend("email.notificacao", dadosConsultaAlterada);

        return new ConsultaDTO(consulta);
    }


    @Transactional
    public ConsultaDTO cancelarConsulta(Long consultaId, String descricaoCancelamento, CategoriaCancelamento motivoCancelamento) {
        Optional<Consulta> consultaOpt = consultaRepository.findById(consultaId);
        Consulta consulta = consultaOpt.orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada"));

        if(!cancelamentoValido(consulta.getDataHora())){
            throw new IllegalArgumentException("Cancelamento deve ser feito com pelo menos 24 horas de antecedência");
        }
        if (consulta.getStatus() != Status.CANCELADA) {
            //String descricao = (descricaoCancelamento != null && !descricaoCancelamento.isBlank()) ? descricaoCancelamento : String.valueOf(descricaoCancelamento);
            consulta.setMotivoCancelamento(motivoCancelamento);
            consulta.setDescricaoCancelamento(descricaoCancelamento);
            consulta.setStatus(Status.CANCELADA);
            consultaRepository.save(consulta);

            DadosConsultaDTO dadosConsulta = new DadosConsultaDTO(
                    consulta.getPaciente().getNome(),
                    consulta.getMedico().getNome(),
                    consulta.getDataHora(),
                    consulta.getPaciente().getUsername(),
                    consulta.getMedico().getUsuario().getUsername(),
                    "CANCELADA"
            );
            this.rabbitTemplate.convertAndSend("email.notificacao", dadosConsulta);
            return new ConsultaDTO(consulta);
        } else {
            throw new IllegalArgumentException("Consulta já foi cancelada");
        }
    }

    public Page<ConsultaDTO> getAllConsultas(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("ROLE_ADMIN"));

        boolean isMedico = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("MEDICO") || a.getAuthority().equals("ROLE_MEDICO"));

        boolean isPaciente = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("PACIENTE") || a.getAuthority().equals("ROLE_PACIENTE"));

        if (isAdmin) {
            return consultaRepository.findAll(pageable).map(ConsultaDTO::new);
        } else if (isMedico) {
            return consultaRepository.findAllByMedicoUsuarioUsername(username, pageable).map(ConsultaDTO::new);
        } else if (isPaciente) {
            return consultaRepository.findAllByPacienteUsuarioUsername(username, pageable).map(ConsultaDTO::new);
        } else {
            return Page.empty(pageable);
        }
    }

    public Page<ConsultaDTO> getAllConsultasPorStatus(Status status, Pageable pageable) {
        return consultaRepository.findAllByStatus(status, pageable).map(ConsultaDTO::new);
    }

    public List<LocalTime> getHorariosDisponiveis(Long medicoId, LocalDate data) {
        if (data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return List.of();
        }

        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado"));

        List<LocalTime> horarios = new ArrayList<>();
        LocalTime horarioAtual = ABERTURA;

        while (horarioAtual.plus(DURACAO).isBefore(FECHAMENTO) || horarioAtual.plus(DURACAO).equals(FECHAMENTO)) {
            horarios.add(horarioAtual);
            horarioAtual = horarioAtual.plus(DURACAO);
        }

        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.atTime(23, 59, 59);

        List<Consulta> consultasAgendadas = consultaRepository.findAllByMedicoAndDataHoraBetween(medico, inicioDia, fimDia);

        List<LocalTime> horariosOcupados = consultasAgendadas.stream()
                .filter(c -> c.getStatus() != Status.CANCELADA)
                .map(c -> c.getDataHora().toLocalTime())
                .toList();

        horarios.removeAll(horariosOcupados);

        if (data.equals(LocalDate.now())) {
            LocalTime agora = LocalTime.now();
            horarios.removeIf(h -> h.isBefore(agora));
        }

        return horarios;
    }

    private boolean pacienteAtivo(Paciente paciente) {
        return paciente.getAtivo();
    }
    private boolean medicoAtivo(Medico medico) {
        return medico.getAtivo();
    }
    private boolean antecedenciaValida(LocalDateTime dataHora) {
        return Duration.between(LocalDateTime.now(), dataHora).compareTo(ANTECEDENCIA_MINIMA) >= 0;
    }
    private boolean cancelamentoValido(LocalDateTime dataHora) {
        return Duration.between(LocalDateTime.now(), dataHora).compareTo(ANTECEDENCIA_MINIMA_CANCELAMENTO) >= 0;
    }
    private boolean horarioValido(LocalDateTime dataHora) {
        DayOfWeek dia = dataHora.getDayOfWeek();
        if (dia == DayOfWeek.SUNDAY) return false;
        LocalTime hora = dataHora.toLocalTime();
        LocalTime fimConsulta = hora.plus(DURACAO);
        return !hora.isBefore(ABERTURA) && !fimConsulta.isAfter(FECHAMENTO);
    }
    private boolean temConsultaNoMesmoDia(Paciente paciente, LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(23, 59, 59);
        return consultaRepository.existsByPacienteAndDataHoraBetween(paciente, inicio, fim);
    }
    private boolean medicoOcupado(Medico medico, LocalDateTime dataHora) {
        LocalDateTime inicioNova = dataHora;
        LocalDateTime fimNova = dataHora.plus(DURACAO);
        LocalDateTime inicioBusca = dataHora.minus(DURACAO);
        LocalDateTime fimBusca = dataHora.plus(DURACAO);
        return consultaRepository.existsByMedicoWithConflict(medico, inicioBusca, fimBusca);
    }

    private boolean medicoOcupadoPorOutraConsulta(Medico medico, LocalDateTime dataHora, Long consultaIdIgnorar) {
        LocalDateTime inicioNova = dataHora;
        LocalDateTime fimNova = dataHora.plus(DURACAO);
        LocalDateTime inicioBusca = dataHora.minus(DURACAO);
        LocalDateTime fimBusca = dataHora.plus(DURACAO);
        
        return consultaRepository.existsByMedicoWithConflictIgnoringId(medico, inicioBusca, fimBusca, consultaIdIgnorar);
    }

    private Optional<Medico> medicoAleatorio(LocalDateTime dataHora) {
        List<Medico> candidatos = medicoRepository.findAllByAtivoTrue();
        candidatos.removeIf(m -> medicoOcupado(m, dataHora));
        if (candidatos.isEmpty()) return Optional.empty();
        return Optional.of(candidatos.get(new Random().nextInt(candidatos.size())));
    }
}
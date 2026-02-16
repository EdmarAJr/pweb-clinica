package br.ifba.edu.clinica.services;

import br.ifba.edu.clinica.dtos.*;
import br.ifba.edu.clinica.entities.*;
import br.ifba.edu.clinica.repositories.PacienteRepository;
import br.ifba.edu.clinica.repositories.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {
    private final PacienteRepository pacienteRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public PacienteService(PacienteRepository pacienteRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.pacienteRepository = pacienteRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Page<PacienteDTO> getAllPacientes(Pageable pageable) {
        return this.pacienteRepository.findAllByAtivoTrue(pageable).map(PacienteDTO::new);
    }

    @Transactional
    public PacienteDTO createPaciente(PacienteFormDTO dados) {
        Usuario usuario = new Usuario();
        usuario.setUsername(dados.username());
        usuario.setPassword(passwordEncoder.encode(dados.password()));
        Role rolePaciente = roleRepository.findByRole("ROLE_PACIENTE");
        if (rolePaciente == null) {
            throw new RuntimeException("Role ROLE_PACIENTE não encontrada. Verifique o DataInitializer.");
        }
        usuario.adicionarRole(rolePaciente);
        Paciente paciente = new Paciente(dados);
        paciente.setUsuario(usuario);
        pacienteRepository.save(paciente);
        return new PacienteDTO(paciente);
    }

    public List<PacienteDTO> getPacienteByNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }
        List<PacienteDTO> pacientes = this.pacienteRepository.findByNomeIlike(nome)
                .stream()
                .map(PacienteDTO::new)
                .toList();
        if (!pacientes.isEmpty()) {
            return pacientes;
        } else {
            return null;
        }
    }

    public PacienteDTO getPacienteById(Long id) {
        Paciente pacienteBanco = this.pacienteRepository.findById(id).orElse(null);
        if (pacienteBanco == null || Boolean.FALSE.equals(pacienteBanco.getAtivo())) {
            return null;
        }
        return new PacienteDTO(pacienteBanco);
    }


    @Transactional
    public PacienteDTO atualizarPaciente(Long id, PacienteUpdateDTO dados) {
        Paciente pacienteBanco = this.pacienteRepository.findById(id).orElse(null);
        if (pacienteBanco == null || dados == null || Boolean.FALSE.equals(pacienteBanco.getAtivo())) {
            return null;
        }
        if (dados.nome() != null) {
            pacienteBanco.setNome(dados.nome());
        }
        if (dados.endereco() != null) {
            var endereco = pacienteBanco.getEndereco();
            var dadosEndereco = dados.endereco();
            if (dadosEndereco.logradouro() != null) endereco.setLogradouro(dadosEndereco.logradouro());
            if (dadosEndereco.numero() != null) endereco.setNumero(dadosEndereco.numero());
            if (dadosEndereco.complemento() != null) endereco.setComplemento(dadosEndereco.complemento());
            if (dadosEndereco.cidade() != null) endereco.setCidade(dadosEndereco.cidade());
            if (dadosEndereco.cep() != null) endereco.setCep(dadosEndereco.cep());
            if (dadosEndereco.estado() != null) endereco.setEstado(dadosEndereco.estado());
        }

        if (dados.telefone() != null) {
            pacienteBanco.setTelefone(dados.telefone());
        }
        if (dados.password() != null && !dados.password().isBlank()) {
             pacienteBanco.getUsuario().setPassword(passwordEncoder.encode(dados.password()));
        }
        this.pacienteRepository.save(pacienteBanco);
        return new PacienteDTO(pacienteBanco);
    }
    public PacienteDTO deletePaciente(Long id) {
        Optional<Paciente> pacienteOp = this.pacienteRepository.findById(id);
        if (pacienteOp.isPresent()) {
            Paciente paciente = pacienteOp.get();
            paciente.setAtivo(false);
            this.pacienteRepository.save(paciente);
            return new PacienteDTO(pacienteOp.get());
        }
        return null;
    }
}
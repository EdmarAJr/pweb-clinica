package br.ifba.edu.clinica.services;

import java.util.List;
import java.util.Optional;

import br.ifba.edu.clinica.dtos.*;
import br.ifba.edu.clinica.entities.*;
import br.ifba.edu.clinica.repositories.RoleRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import br.ifba.edu.clinica.repositories.MedicoRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicoService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
	private final MedicoRepository medicoRepository;
    private final RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

	public MedicoService(MedicoRepository medicoRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.medicoRepository = medicoRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
	}

	public Page<MedicoDTO> getAllMedicos(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            return this.medicoRepository.findAll(pageable).map(MedicoDTO::new);
        } else {
            return this.medicoRepository.findAllByAtivoTrue(pageable).map(MedicoDTO::new);
        }
    }

    public MedicoDTO getMedicoById(Long id) {
        Medico medicoBanco = this.medicoRepository.findById(id).orElse(null);
        if (medicoBanco == null || Boolean.FALSE.equals(medicoBanco.getAtivo())) {
            return null;
        }
        return new MedicoDTO(medicoBanco);
    }

    @Transactional
	public MedicoDTO createMedico(MedicoFormDTO dados) {
        Usuario usuario = new Usuario();
        usuario.setUsername(dados.username());
        usuario.setPassword(passwordEncoder.encode(dados.password()));
        Role roleMedico = roleRepository.findByRole("ROLE_MEDICO");
        if (roleMedico == null) {
            throw new RuntimeException("Role ROLE_MEDICO não encontrada. Verifique o DataInitializer.");
        }
        usuario.adicionarRole(roleMedico);
        Medico medico = new Medico(dados);
        medico.setUsuario(usuario);
        medico.setAtivo(false);
        medicoRepository.save(medico);
        return new MedicoDTO(medico);
	}

    @Transactional
    public MedicoDTO ativarMedico(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado"));
        
        if (Boolean.TRUE.equals(medico.getAtivo())) {
            throw new IllegalArgumentException("Médico já está ativo");
        }

        medico.setAtivo(true);
        medico.setValidado(true);
        medicoRepository.save(medico);

        DadosNotificacaoMedicoDTO dadosNotificacao = new DadosNotificacaoMedicoDTO(medico.getNome(), medico.getEmail());
        this.rabbitTemplate.convertAndSend("email.ativacao", dadosNotificacao);

        return new MedicoDTO(medico);
    }

	public List<MedicoDTO> getMedicoByNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }
        List<MedicoDTO> medicos = this.medicoRepository.findByNomeIlike(nome)
                .stream()
                .map(MedicoDTO::new)
                .toList();
        if (!medicos.isEmpty()) {
            return medicos;
        } else {
            return null;
        }
    }

    @Transactional
	public MedicoDTO atualizarMedico(Long id, MedicoUpdateDTO dados) {
		Medico medicoBanco = this.medicoRepository.findById(id).orElse(null);
        if (medicoBanco == null || dados == null || Boolean.FALSE.equals(medicoBanco.getAtivo())) {
            return null;
        }
        if (dados.nome() != null) {
            medicoBanco.setNome(dados.nome());
        }
        if (dados.endereco() != null) {
            var endereco = medicoBanco.getEndereco();
            var dadosEndereco = dados.endereco();

            if (dadosEndereco.logradouro() != null) endereco.setLogradouro(dadosEndereco.logradouro());
            if (dadosEndereco.numero() != null) endereco.setNumero(dadosEndereco.numero());
            if (dadosEndereco.complemento() != null) endereco.setComplemento(dadosEndereco.complemento());
            if (dadosEndereco.cidade() != null) endereco.setCidade(dadosEndereco.cidade());
            if (dadosEndereco.cep() != null) endereco.setCep(dadosEndereco.cep());
            if (dadosEndereco.estado() != null) endereco.setEstado(dadosEndereco.estado());
        }
        if (dados.telefone() != null) {
            medicoBanco.setTelefone(dados.telefone());
        }
        if (dados.password() != null && !dados.password().isBlank()) {
            medicoBanco.getUsuario().setPassword(passwordEncoder.encode(dados.password()));
        }

        this.medicoRepository.save(medicoBanco);
		return new MedicoDTO(medicoBanco);
	}
	
	public MedicoDTO deleteMedico(Long id) {
		Optional<Medico> medicoOp = this.medicoRepository.findById(id);
		if (medicoOp.isPresent()) {
            Medico medico = medicoOp.get();
            medico.setAtivo(false);
            medico.setValidado(false);
            this.medicoRepository.save(medico);
			return new MedicoDTO(medicoOp.get());
		}
		return null;
	}
}
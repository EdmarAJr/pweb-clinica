package br.ifba.edu.clinica.services;

import java.util.List;

import br.ifba.edu.clinica.dtos.MedicoDTO;
import br.ifba.edu.clinica.repositories.MedicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.ifba.edu.clinica.dtos.LoginDTO;
import br.ifba.edu.clinica.dtos.UsuarioDTO;
import br.ifba.edu.clinica.entities.Usuario;
import br.ifba.edu.clinica.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private MedicoRepository medicoRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, MedicoRepository medicoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.medicoRepository = medicoRepository;
    }

    public UsuarioDTO cadastrarUsuario(LoginDTO loginDTO) {
        var usuario = new Usuario(loginDTO);
        usuario.setPassword(passwordEncoder.encode(loginDTO.password()));
        usuarioRepository.save(usuario);

        return new UsuarioDTO(usuario);
    }

    public UsuarioDTO apagarUsuario(Long id) {
        var usuario = usuarioRepository.getReferenceById(id);
        usuarioRepository.deleteById(id);
        return new UsuarioDTO(usuario);
    }

    public Page<UsuarioDTO> getAllUsuarios(Pageable pageable) {
        return this.usuarioRepository.findAll(pageable).map(UsuarioDTO::new);
    }

}
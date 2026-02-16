package br.ifba.edu.clinica.dtos;

import br.ifba.edu.clinica.entities.Usuario;

public record UsuarioDTO(Long id, String username) {
    
    public UsuarioDTO(Usuario usuario) { 
        this(usuario.getId(), usuario.getUsername());
    }
}

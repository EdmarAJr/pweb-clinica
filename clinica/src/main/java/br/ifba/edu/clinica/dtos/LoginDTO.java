package br.ifba.edu.clinica.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "O email de usuário é obrigatório.")
        String username,
        
        @NotBlank(message = "A senha é obrigatória.")
        String password) {
}
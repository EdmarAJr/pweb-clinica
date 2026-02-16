package br.ifba.edu.clinica.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Objeto utilizado para atualizar dados de um paciente (somente campos permitidos)")
public record PacienteUpdateDTO(
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        @Schema(description = "Nome completo do paciente", example = "Ana Maria Ribeiro")
        String nome,

        @Size(min = 3, max = 100, message = "A senha deve ter entre 3 e 100 caracteres")
        @Schema(description = "Password do paciente", example = "132456")
        String password,

        @Schema(description = "Telefone de contato", example = "71999998888 (apenas os números)")
        @Size(min = 9, max = 15, message = "O telefone deve ter entre 9 e 15 caracteres")
        String telefone,

        @Schema(description = "Endereço do paciente (opcional)")
        EnderecoFormDTO endereco) {
}
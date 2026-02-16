package br.ifba.edu.clinica.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Objeto utilizado para atualizar dados de um médico (somente campos permitidos)")
public record MedicoUpdateDTO(
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        @Schema(description = "Nome completo do médico", example = "Dra. Ana Paula Ribeiro")
        String nome,

        @Size(min = 3, max = 100, message = "A senha deve ter entre 3 e 100 caracteres")
        @Schema(description = "Password do paciente", example = "132456")
        String password,

        @Size(min = 9, max = 15, message = "O telefone deve ter entre 9 e 15 caracteres")
        @Schema(description = "Telefone de contato", example = "(71) 99999-8888")
        String telefone,

        @Schema(description = "Endereço do médico (opcional)")
        EnderecoFormDTO endereco) {

    public MedicoUpdateDTO() {
        this(null, null, null, null);
    }
}
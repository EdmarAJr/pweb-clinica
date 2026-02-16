package br.ifba.edu.clinica.dtos;

import br.ifba.edu.clinica.entities.Paciente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Objeto utilizado para cadastrar ou atualizar dados de um paciente")
public record PacienteFormDTO(
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        @Schema(description = "Nome completo do paciente", example = "Ana Maria Ribeiro")
        @NotBlank(message = "O nome não pode ser nulo")
        String nome,

        @Schema(description = "E-mail do paciente", example = "ana.maria@email.com")
        @Email(message = "E-mail inválido")
        String username,

        @Size(min = 3, max = 100, message = "A senha deve ter entre 3 e 100 caracteres")
        @NotBlank(message = "A senha não pode ser nula")
        @Schema(description = "Senha do paciente", example = "123456")
        String password,

        @Schema(description = "Número do CPF do paciente", example = "123.456.789-00")
        @NotBlank(message = "O cpf não pode ser nulo")
        @Size(min = 11, max = 14, message = "O CPF deve ter exatamente 11 caracteres")
        String cpf,

        @Schema(description = "Endereço do paciente")
        EnderecoFormDTO endereco,

        @NotBlank(message = "O telefone não pode ser nulo")
        @Schema(description = "Telefone de contato", example = "(71) 99999-8888")
        @Size(min = 9, max = 15, message = "O telefone deve ter entre 9 e 15 caracteres")
        String telefone
        ) {

    public PacienteFormDTO(Paciente paciente) {
        this(
                paciente.getNome(),
                paciente.getUsuario() != null ? paciente.getUsuario().getUsername() : null,
                null,
                paciente.getCpf(),
                paciente.getEndereco() != null ? new EnderecoFormDTO(paciente.getEndereco()) : null,
                paciente.getTelefone()
        );
    }
}
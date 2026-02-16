package br.ifba.edu.clinica.dtos;

import br.ifba.edu.clinica.entities.Endereco;
import br.ifba.edu.clinica.entities.Paciente;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto de resposta com os dados do paciente cadastrado")
public record PacienteDTO(
        @Schema(description = "Identificador único do paciente", example = "1")
        Long id,

        @Schema(description = "Nome completo do paciente", example = "Ana Maria Ribeiro")
        String nome,

        @Schema(description = "E-mail do paciente", example = "ana.maria@email.com")
        String username,

        @Schema(description = "Número do CPF do paciente", example = "12345678900")
        String cpf,

        @Schema(description = "Endereço do paciente")
        Endereco endereco,

        @Schema(description = "Telefone de contato", example = "(71) 99999-8777")
        String telefone) {

    public PacienteDTO(Paciente paciente) {
        this(paciente.getId(),
                paciente.getNome(),
                paciente.getUsername(),
                paciente.getCpf(),
                paciente.getEndereco(),
                paciente.getTelefone()
        );
    }
}
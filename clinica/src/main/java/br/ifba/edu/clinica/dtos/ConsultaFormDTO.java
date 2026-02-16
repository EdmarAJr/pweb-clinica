package br.ifba.edu.clinica.dtos;

import br.ifba.edu.clinica.entities.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Objeto utilizado para cadastrar ou atualizar dados de uma consulta")
public record ConsultaFormDTO(
        @Schema(description = "ID do paciente associado à consulta (Obrigatório apenas para ADMIN)", example = "1")
        Long idPaciente,

        @Schema(description = "ID do médico associado à consulta", example = "2")
        Long idMedico,

        @Schema(description = "Data e hora da consulta", example = "2024-07-01 14:00")
        @NotNull(message = "A data e horário não podem ser nulos")
        @Future(message = "A data da consulta deve ser futura")
        LocalDateTime dataHora,

        @Schema(description = "Descrição da consulta", example = "Consulta de rotina")
        @NotNull(message = "A descrição é obrigatória")
        String descricao,

        @Schema(description = "Email do paciente", example = "mariana.ribeiro@email.com")
        String email){

    public ConsultaFormDTO(Long idPaciente, Long idMedico, LocalDateTime dataHora, String descricao, String email) {
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.email = email;
    }
}
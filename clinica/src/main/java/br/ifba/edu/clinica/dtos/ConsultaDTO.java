package br.ifba.edu.clinica.dtos;

import br.ifba.edu.clinica.entities.CategoriaCancelamento;
import br.ifba.edu.clinica.entities.Consulta;
import br.ifba.edu.clinica.entities.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

@Schema(description = "Objeto de resposta com os dados das consultas cadastradas")
public record ConsultaDTO(
        @Schema(description = "Identificador único do consulta", example = "1")
        Long id,

        @Schema(description = "Identificador único do paciente", example = "1")
        Long idPaciente,

        @Schema(description = "Nome do paciente", example = "João Silva")
        String nomePaciente,

        @Schema(description = "CPF do paciente", example = "12345678900")
        String cpfPaciente,

        @Schema(description = "Identificador único do médico", example = "1")
        Long idMedico,

        @Schema(description = "Nome do médico", example = "Dra. Ana")
        String nomeMedico,

        @Schema(description = "Data e hora da consulta", example = "2024-07-01 14:00")
        LocalDateTime dataHora,

        @Schema(description = "Motivo da consulta", example = "Consulta de rotina")
        String descricao,

        @Schema(description = "Status da consulta", example = "AGENDADA, CANCELADA, CONCLUIDA")
        Status status,

        @Schema(description = "Motivo do cancelamento da consulta", example = "OUTROS, DESISTÊNCIA, CANCELAMENTO")
        String motivoCancelamento,

        @Schema(description = "Motivo do cancelamento da consulta", example = "Indisposição")
        String descricaoCancelamento) {

    public ConsultaDTO(Consulta consulta) {
        this(consulta.getId(),
                consulta.getPaciente().getId(),
                consulta.getPaciente().getNome(),
                consulta.getCpf(), 
                consulta.getMedico().getId(),
                consulta.getMedico().getNome(),
                consulta.getDataHora(),
                consulta.getDescricao(),
                consulta.getStatus(),
                consulta.getMotivoCancelamento() != null ? consulta.getMotivoCancelamento().toString() : null,
                consulta.getDescricaoCancelamento() != null ? consulta.getDescricaoCancelamento() : null
        );
    }
}
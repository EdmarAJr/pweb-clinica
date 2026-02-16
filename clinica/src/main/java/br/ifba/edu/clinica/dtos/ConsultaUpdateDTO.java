package br.ifba.edu.clinica.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ConsultaUpdateDTO(
        @NotNull(message = "A nova data e horário são obrigatórios")
        @Future(message = "A nova data ou horário da consulta deve ser futura")
        LocalDateTime dataHora
) {
}
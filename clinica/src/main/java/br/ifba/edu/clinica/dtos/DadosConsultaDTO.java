package br.ifba.edu.clinica.dtos;

import java.time.LocalDateTime;

public record DadosConsultaDTO(String nomePaciente, String nomeMedico, LocalDateTime dataHora, String emailPaciente, String emailMedico, String statusConsulta) {
}
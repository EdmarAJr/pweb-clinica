package br.ifba.edu.clinica.entities;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Especialidades médicas disponíveis")
public enum EspecialidadeMedico {
	CARDIOLOGIA,
	ORTOPEDIA,
	GINECOLOGIA,
	DERMATOLOGIA;
}
package br.ifba.edu.clinica.dtos;

import br.ifba.edu.clinica.entities.CategoriaCancelamento;
import jakarta.validation.constraints.NotNull;

public record CancelamentoConsultaDTO(
        @NotNull(message = "A categoria do cancelamento é obrigatória")
        CategoriaCancelamento motivoCancelamento,
        String descricaoCancelamento
) {
}
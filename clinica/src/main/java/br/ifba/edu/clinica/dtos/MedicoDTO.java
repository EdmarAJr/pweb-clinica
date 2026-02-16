package br.ifba.edu.clinica.dtos;

import br.ifba.edu.clinica.entities.Endereco;
import br.ifba.edu.clinica.entities.EspecialidadeMedico;
import br.ifba.edu.clinica.entities.Medico;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto de resposta com os dados do médico cadastrado")
public record MedicoDTO(
        @Schema(description = "Identificador único do médico", example = "1")
        Long id,

        @Schema(description = "Nome completo do médico", example = "Dra. Ana Paula Ribeiro")
        String nome,

        @Schema(description = "E-mail do médico", example = "ana.ribeiro@clinica.com")
        String username,

        @Schema(description = "Número do CRM do médico", example = "123456/BA")
        String crm,

        @Schema(description = "Endereço do médico")
        Endereco endereco,

        @Schema(description = "Telefone de contato", example = "(71) 99999-8888")
        String telefone,

        Boolean ativo,
        
        @Schema(description = "Especialidade do médico", example = "CARDIOLOGIA")
        EspecialidadeMedico especialidade){


    public MedicoDTO(Medico medico) {
        this(medico.getId(),
                medico.getNome(),
                medico.getUsuario() != null ? medico.getUsuario().getUsername() : null,
                medico.getCrm(),
                medico.getEndereco(),
                medico.getTelefone(),
                medico.getAtivo(),
                medico.getEspecialidade()
                );
    }
}
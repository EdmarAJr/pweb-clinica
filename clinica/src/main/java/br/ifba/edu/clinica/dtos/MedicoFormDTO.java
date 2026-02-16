package br.ifba.edu.clinica.dtos;

import br.ifba.edu.clinica.dtos.EnderecoFormDTO;
import br.ifba.edu.clinica.entities.EspecialidadeMedico;
import br.ifba.edu.clinica.entities.Medico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


@Schema(description = "Objeto utilizado para cadastrar ou atualizar dados de um médico")
public record MedicoFormDTO(
                    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
                    @Schema(description = "Nome completo do médico", example = "Dra. Ana Paula Ribeiro")
                    @NotBlank(message = "O nome não pode ser nulo")
                    String nome,

                    @Schema(description = "E-mail do médico", example = "ana.ribeiro@clinica.com")
                    @NotBlank(message = "O email não pode ser nulo")
                    @Email(message = "E-mail inválido")
                    String username,

                    @Size(min = 3, max = 100, message = "A senha deve ter entre 3 e 100 caracteres")
                    @NotBlank(message = "A senha não pode ser nula")
                    @Schema(description = "Senha do paciente", example = "123456")
                    String password,

                    @Schema(description = "Número do CRM do médico", example = "123456/BA")
                    @NotBlank(message = "O crm não pode ser nulo")
                    @Size(min = 9, max = 9, message = "O crm deve ter 9 caracteres")
                    String crm,

                    @Schema(description = "Endereço do médico")
                    EnderecoFormDTO endereco,

                    @NotBlank(message = "O telefone não pode ser nulo")
                    @Schema(description = "Telefone de contato", example = "(71) 99999-8888")
                    @Size(min = 9, max = 15, message = "O telefone deve ter entre 9 e 15 caracteres")
                    String telefone,

                    @Schema(description = "Especialidade do médico", example = "CARDIOLOGIA")
                    @NotNull(message = "A especialida não pode ser nula")
                    EspecialidadeMedico especialidade) {

    public MedicoFormDTO(Medico medico) {
        this(
            medico.getNome(),
            medico.getUsuario() != null ? medico.getUsuario().getUsername() : null,
                null, 
            medico.getCrm(), medico.getEndereco() != null ? new EnderecoFormDTO(medico.getEndereco()) : null,
            medico.getTelefone(),
            medico.getEspecialidade()
        );
    }

}
package br.ifba.edu.clinica.dtos;

import br.ifba.edu.clinica.entities.Endereco;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Objeto representando o endereço (logradouro, número, bairro, complemento, cidade, estado, cep)")
public record EnderecoFormDTO(
        @Schema(description = "Logradouro", example = "Rua das Flores")
        @NotBlank(message = "A rua não pode ser nula")
        @Size(min = 1, max = 200)
        String logradouro,

        @Schema(description = "Número", example = "123A")
        String numero,
        
        @Schema(description = "Complemento", example = "123A")
        String complemento,

        @Schema(description = "Bairro", example = "Brotas")
        String bairro,

        @Schema(description = "Cidade", example = "Salvador")
        @NotBlank(message = "A cidade não pode ser nula")
        String cidade,

        @Schema(description = "Estado", example = "BA")
        @NotBlank(message = "O estado não pode ser nulo")
        String estado,

        @Schema(description = "CEP", example = "40000-000")
        @NotBlank(message = "O CEP não pode ser nulo")
        @Size(min = 8, max = 10, message = "O CEP deve ter entre 8 e 10 caracteres")
        String cep) {

    public EnderecoFormDTO(Endereco endereco) {
        this(endereco.getLogradouro(), endereco.getNumero(), endereco.getBairro(), endereco.getComplemento(), endereco.getCidade(), endereco.getEstado(), endereco.getCep());
    }
}
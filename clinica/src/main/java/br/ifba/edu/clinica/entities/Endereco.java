package br.ifba.edu.clinica.entities;

import br.ifba.edu.clinica.dtos.EnderecoFormDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class Endereco {
    @Column(name = "logradouro")
    @NotBlank(message = "O logradouro é obigatório")
    private String logradouro;
	
    @Column(name = "numero")
    private String numero;
	
	@Column(name = "complemento")
    private String complemento;

    @NotBlank(message = "O bairro é obigatório")
    @Column(name = "bairro")
    private String bairro;
	
    @Column(name = "cidade")
    @NotBlank(message = "A cidade é obigatório")
    private String cidade;
	
	@Column(name = "estado")
	@NotBlank(message = "O estado é obigatório")
    private String estado;
    
	@Column(name = "cep")
	@NotBlank(message = "O cep é obigatório")
    private String cep;

    public Endereco() { }

    public Endereco(String logradouro, String numero, String bairro, String complemento, String cidade, String estado, String cep) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    public Endereco(EnderecoFormDTO dto) {
        if (dto == null) return;
        this.logradouro = dto.logradouro();
        this.numero = dto.numero();
        this.complemento = dto.complemento();
        this.bairro = dto.bairro();
        this.cidade = dto.cidade();
        this.estado = dto.estado();
        this.cep = dto.cep();
    }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
}
package br.ifba.edu.clinica.entities;

import br.ifba.edu.clinica.dtos.PacienteFormDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity(name = "pacientes")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do paciente", example = "1")
    private Long id;

    @Schema(description = "Nome completo do paciente", example = "Mariana  Ribeiro")
    private String nome;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @Column(nullable = false, unique = true)
    @Schema(description = "Número do CPF do paciente", example = "12345678900")
    private String cpf;
    
    @Embedded
    private Endereco endereco;

    @Schema(description = "Telefone de contato", example = "(71) 99999-8888")
    private String telefone;

    @Column(nullable = false)
    private Boolean ativo;

    public Paciente() {
        super();
    }

    public Paciente(Long id, Usuario usuario, String nome, String cpf, Endereco endereco, String telefone) {
        super();
        this.id = id;
        this.usuario = usuario;
        this.nome = nome;
        this.cpf = cpf;
        this.endereco = endereco;
        this.telefone = telefone;
        this.ativo = true;
    }

    public Paciente(PacienteFormDTO pacienteForm) {
        super();
        this.nome = pacienteForm.nome();
        this.cpf = pacienteForm.cpf();
        this.endereco = pacienteForm.endereco() != null ? new Endereco(pacienteForm.endereco()) : null;
        this.telefone = pacienteForm.telefone();
        this.ativo = true;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public String getUsername() {
        return usuario != null ? usuario.getUsername() : null;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean status) { this.ativo = status; }
    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
}

package br.ifba.edu.clinica.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "consultas")
public class Consulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do consulta", example = "1")
    Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    @JsonIgnoreProperties("consultas") // evita recursão ao serializar Paciente
    @Schema(description = "Paciente associado à consulta")
    private Paciente paciente;

    @Column(name = "cpf_paciente", nullable = false)
    @Schema(description = "CPF do paciente associado à consulta", example = "154.419.843-16")
    private String cpf;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medico", nullable = false)
    @JsonIgnoreProperties("consultas") // evita recursão ao serializar Medico
    @Schema(description = "Médico associado à consulta")
    private Medico medico;
    
    @Schema(description = "Data e hora da consulta", example = "2024-07-01T14:30:00")
    @Column(name = "dataHora")
    private LocalDateTime dataHora;

    @Schema(description = "Descrição da consulta", example = "Consulta de rotina")
    private String descricaoConsulta;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Status da consulta", example = "AGENDADA, CANCELADA, CONCLUÍDA")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Motivo do cancelamento da consulta", example = "OUTROS, DESISTÊNCIA, CANCELAMENTO")
    private CategoriaCancelamento motivoCancelamento;

    @Schema(description = "Motivo do cancelamento da consulta", example = "Indisposição")
    private String descricaoCancelamento;

    public Consulta() { }

    public Consulta(Paciente paciente, Medico medico, LocalDateTime dataHora, String descricaoConsulta) {
        this.paciente = paciente;
        this.cpf = paciente.getCpf();
        this.medico = medico;
        this.dataHora = dataHora;
        this.descricaoConsulta = descricaoConsulta;
        this.status = Status.AGENDADA;
        this.motivoCancelamento = null;
        this.descricaoCancelamento = null;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Paciente getPaciente() {
        return paciente;
    }
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        if (paciente != null) {
            this.cpf = paciente.getCpf();
        }
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public Medico getMedico() {
        return medico;
    }
    public void setMedico(Medico medico) {
        this.medico = medico;
    }
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    public String getDescricao() {
        return descricaoConsulta;
    }
    public void setDescricao(String descricaoConsulta) {
        this.descricaoConsulta = descricaoConsulta;
    }
        public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public CategoriaCancelamento getMotivoCancelamento() {
        return motivoCancelamento;
    }
    public void setMotivoCancelamento(CategoriaCancelamento motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }
    public String getDescricaoCancelamento() {
        return descricaoCancelamento;
    }
    public void setDescricaoCancelamento(String descricaoCancelamento) {
        this.descricaoCancelamento = descricaoCancelamento;
    }
}
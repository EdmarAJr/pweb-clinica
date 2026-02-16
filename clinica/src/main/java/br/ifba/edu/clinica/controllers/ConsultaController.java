package br.ifba.edu.clinica.controllers;

import br.ifba.edu.clinica.dtos.CancelamentoConsultaDTO;
import br.ifba.edu.clinica.dtos.ConsultaDTO;
import br.ifba.edu.clinica.dtos.ConsultaFormDTO;
import br.ifba.edu.clinica.dtos.ConsultaUpdateDTO;
import br.ifba.edu.clinica.entities.CategoriaCancelamento;
import br.ifba.edu.clinica.entities.Status;
import br.ifba.edu.clinica.services.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping ("/consultas")
@Tag(name = "Consultas", description = "Endpoints para gerenciar consultas")
public class ConsultaController {
    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @Operation(summary = "Listar consultas", description = "Retorna todas as consultas disponíveis")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de consultas paginada"),
            @ApiResponse(responseCode = "204", description = "Dados vazios"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/listar")
    public Page<ConsultaDTO> getAllConsultas(
            @ParameterObject
            @Parameter(description = "Parâmetros de paginação e ordenação")
            @PageableDefault(size = Integer.MAX_VALUE, sort = "dataHora", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(this.consultaService.getAllConsultas(pageable)).getBody();
    }

    @Operation(summary = "Listar consultas por status", description = "Retorna as consultas por status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de consultas por status paginada"),
            @ApiResponse(responseCode = "204", description = "Dados vazios"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/listarPorStatus")
    public Page<ConsultaDTO> getConsultasByStatus(
            @Parameter(description = "Parâmetros de status", example = "AGENDADA")
            Status status,
            @ParameterObject
            @Parameter(description = "Parâmetros de paginação e ordenação")
            @PageableDefault(sort = "dataHora", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(this.consultaService.getAllConsultasPorStatus(status, pageable)).getBody();
    }

    @Operation(summary = "Agendar consulta", description = "Cadastra uma nova consulta no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consulta agendada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping("/agendar")
    @Transactional
    public ResponseEntity<ConsultaDTO> agendarConsulta(@RequestBody @Valid ConsultaFormDTO consulta) {
        ConsultaDTO novaConsulta = this.consultaService.agendar(
                consulta.idPaciente(),
                consulta.idMedico() != null ? consulta.idMedico() : null,
                consulta.dataHora(),
                consulta.descricao(),
                consulta.email()
        );
        return ResponseEntity.status(201).body(novaConsulta);
    }

    @Operation(summary = "Concluir consulta", description = "Altera o status da consulta para concluída")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta concluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ConsultaDTO> concluirConsulta(@PathVariable Long id) {
        ConsultaDTO consultaConcluida = this.consultaService.concluirConsulta(id);
        if (consultaConcluida != null) {
            return ResponseEntity.ok(consultaConcluida);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Reagendar consulta", description = "Altera a data e hora de uma consulta agendada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta reagendada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou conflito de horário"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping("/reagendar/{id}")
    @Transactional
    public ResponseEntity<ConsultaDTO> reagendarConsulta(@PathVariable Long id, @RequestBody @Valid ConsultaUpdateDTO dados) {
        ConsultaDTO consultaReagendada = this.consultaService.alterarDataHoraConsulta(id, dados.dataHora());
        if (consultaReagendada != null) {
            return ResponseEntity.ok(consultaReagendada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Cancelar consulta", description = "Cancela consulta de um paciente agendada com um médico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta cancelada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ConsultaDTO> cancelarConsulta(@PathVariable Long id, @RequestBody @Valid CancelamentoConsultaDTO dados) {
        ConsultaDTO consultaCancelada = this.consultaService.cancelarConsulta(id, dados.descricaoCancelamento(), dados.motivoCancelamento());
        if (consultaCancelada != null) {
            return ResponseEntity.ok(consultaCancelada);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @Operation(summary = "Listar horários disponíveis", description = "Retorna os horários disponíveis para um médico em uma data específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de horários disponíveis"),
            @ApiResponse(responseCode = "404", description = "Médico não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/disponibilidade")
    public ResponseEntity<List<LocalTime>> getHorariosDisponiveis(
            @Parameter(description = "ID do médico", example = "1") @RequestParam Long medicoId,
            @Parameter(description = "Data da consulta", example = "2024-07-01") @RequestParam LocalDate data) {
        return ResponseEntity.ok(this.consultaService.getHorariosDisponiveis(medicoId, data));
    }
}
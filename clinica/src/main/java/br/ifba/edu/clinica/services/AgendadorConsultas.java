package br.ifba.edu.clinica.services;

import br.ifba.edu.clinica.entities.Consulta;
import br.ifba.edu.clinica.entities.Status;
import br.ifba.edu.clinica.repositories.ConsultaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AgendadorConsultas {

    private final ConsultaRepository consultaRepository;

    public AgendadorConsultas(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    @Scheduled(fixedRate = 300000) // Executa a cada 5 minutos (300.000 ms)
    @Transactional
    public void finalizarConsultasExpiradas() {
        LocalDateTime limite = LocalDateTime.now().minusHours(1);

        List<Consulta> consultasExpiradas = consultaRepository.findAllByStatusAndDataHoraBefore(limite);

        if (!consultasExpiradas.isEmpty()) {
            for (Consulta consulta : consultasExpiradas) {
                consulta.setStatus(Status.CONCLUíDA);
            }
            consultaRepository.saveAll(consultasExpiradas);
            System.out.println("Agendador: " + consultasExpiradas.size() + " consultas foram finalizadas automaticamente.");
        }
    }
}
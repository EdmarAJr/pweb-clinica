package br.ifba.edu.clinica.repositories;

import br.ifba.edu.clinica.entities.Consulta;
import br.ifba.edu.clinica.entities.Medico;
import br.ifba.edu.clinica.entities.Paciente;
import br.ifba.edu.clinica.entities.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    Page<Consulta> findAllByStatus(Status status, Pageable pageable);
    Page<Consulta> findAll(Pageable pageable);
    @Query(value = "select c.* from consultas c join pacientes p on c.idPaciente = p.id where p.nome ilike concat('%', :nome, '%')", nativeQuery = true)
    List<Consulta> findByNomeIlike(@Param("nome") String nome);
    boolean existsByPacienteAndDataHoraBetween(Paciente paciente, LocalDateTime inicio, LocalDateTime fim);
    boolean existsByMedicoAndDataHora(Medico medico, LocalDateTime dataHora);

    @Query("select count(c) > 0 from consultas c where c.medico = :medico and c.dataHora > :inicio and c.dataHora < :fim and c.status != 'CANCELADA'")
    boolean existsByMedicoWithConflict(@Param("medico") Medico medico, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("select count(c) > 0 from consultas c where c.medico = :medico and c.dataHora > :inicio and c.dataHora < :fim and c.status != 'CANCELADA' and c.id != :consultaId")
    boolean existsByMedicoWithConflictIgnoringId(@Param("medico") Medico medico, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, @Param("consultaId") Long consultaId);

    Page<Consulta> findAllByMedicoUsuarioUsername(String username, Pageable pageable);
    Page<Consulta> findAllByPacienteUsuarioUsername(String username, Pageable pageable);

    List<Consulta> findAllByMedicoAndDataHoraBetween(Medico medico, LocalDateTime inicio, LocalDateTime fim);

    @Query("select c from consultas c where c.status in ('AGENDADA', 'EM_ANDAMENTO') and c.dataHora < :limite")
    List<Consulta> findAllByStatusAndDataHoraBefore(@Param("limite") LocalDateTime limite);
}
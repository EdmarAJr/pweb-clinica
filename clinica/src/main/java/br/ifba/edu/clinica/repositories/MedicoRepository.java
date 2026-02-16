package br.ifba.edu.clinica.repositories;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.ifba.edu.clinica.dtos.MedicoDTO;
import br.ifba.edu.clinica.entities.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
	List<Medico> findByNomeContaining(String nome);
    Page<Medico> findAllByAtivoTrue(Pageable pageable);
    @Query(value = "select * from medicos where nome ilike %:nome%", nativeQuery = true)
    List<Medico> findByNomeIlike(@Param("nome") String nome);
    List<Medico> findAllByAtivoTrue();
    Medico findByUsuarioUsername(String username);
}
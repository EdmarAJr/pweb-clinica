package br.ifba.edu.clinica.repositories;

import br.ifba.edu.clinica.entities.Paciente;
import br.ifba.edu.clinica.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    List<Paciente> findByNomeContaining(String nome);
    Page<Paciente> findAllByAtivoTrue(Pageable pageable);
    @Query(value = "select * from pacientes where nome ilike %:nome%", nativeQuery = true)
    List<Paciente> findByNomeIlike(@Param("nome") String nome);
    Optional<Paciente> findByUsuario(Usuario usuario);
    Optional<Paciente> findByUsuarioUsername(String username);
}
package br.ifba.edu.clinica.repositories;

import br.ifba.edu.clinica.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    UserDetails findByUsername(String username);
}

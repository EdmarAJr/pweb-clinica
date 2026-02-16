package br.ifba.edu.clinica.repositories;

import br.ifba.edu.clinica.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(String role);
}

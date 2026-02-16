package br.ifba.edu.email_service.repositories;

import br.ifba.edu.email_service.entities.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long>{
}

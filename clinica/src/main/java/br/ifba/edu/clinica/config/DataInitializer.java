package br.ifba.edu.clinica.config;

import br.ifba.edu.clinica.entities.Role;
import br.ifba.edu.clinica.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        criarRoleSeNaoExistir("ROLE_ADMIN");
        criarRoleSeNaoExistir("ROLE_MEDICO");
        criarRoleSeNaoExistir("ROLE_PACIENTE");
    }

    private void criarRoleSeNaoExistir(String nome) {
        if (roleRepository.findByRole(nome) == null) {
            roleRepository.save(new Role(null, nome));
        }
    }
}

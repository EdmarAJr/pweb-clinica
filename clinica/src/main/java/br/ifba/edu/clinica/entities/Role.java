package br.ifba.edu.clinica.entities;

import org.springframework.security.core.GrantedAuthority;
import br.ifba.edu.clinica.dtos.RoleDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String role;

    public Role(RoleDTO roleDto) {
        this.id = roleDto.id();
        this.role = roleDto.role();
    }
    public Role() {
        super();
    }
    public Role(Long id, String role) {
        super();
        this.id = id;
        this.role = role;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    @Override
    public String getAuthority() {
        return role;
    }
}

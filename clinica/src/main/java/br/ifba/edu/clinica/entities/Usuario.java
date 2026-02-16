package br.ifba.edu.clinica.entities;

import br.ifba.edu.clinica.dtos.LoginDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "usuarios")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Schema(description = "E-mail do usuário", example = "mariana.ribeiro@email.com")
    private String username;

    @Schema(description = "Senha do usuário", example = "123456")
    private String password;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_roles", 
            joinColumns = @JoinColumn(name = "idUsuarios"), 
            inverseJoinColumns = @JoinColumn(name = "idRoles")
    )
    private List<Role> roles= new ArrayList<Role>();

    public Usuario() {
        super();
    }

    public Usuario(Long id, String username, String password) {
        super();
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Usuario(LoginDTO dto) {
        this.username = dto.username();
        this.password = dto.password();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }
    @Override
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String getUsername() { return username; }

    public void setUsername(String username) {
        this.username = username;
    }
    public void adicionarRole(Role role) {
        this.roles.add(role);
    }

    public List<Role> getRoles() {
        return roles;
    }
}
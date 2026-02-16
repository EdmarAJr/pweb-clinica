package br.ifba.edu.clinica.services;

import br.ifba.edu.clinica.entities.Medico;
import br.ifba.edu.clinica.entities.Paciente;
import br.ifba.edu.clinica.repositories.MedicoRepository;
import br.ifba.edu.clinica.repositories.PacienteRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import br.ifba.edu.clinica.entities.Usuario;

@Service
public class JWTokenService {
    @Value("${jwt.secret}")
    private String secret;  
    private static final String ISSUER = "API da clínica";

    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private MedicoRepository medicoRepository;

    public String gerarToken(Usuario usuario) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            List<String> roles = usuario.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            var jwtBuilder = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getUsername())
                    .withClaim("roles", roles)
                    .withExpiresAt(dataExpiracao());

            // Adiciona ID do Paciente se for paciente
            if (roles.contains("ROLE_PACIENTE")) {
                Optional<Paciente> paciente = pacienteRepository.findByUsuario(usuario);
                paciente.ifPresent(p -> jwtBuilder.withClaim("pacienteId", p.getId()));
            }

            // Adiciona ID do Médico se for médico
            if (roles.contains("ROLE_MEDICO")) {
                Medico medico = medicoRepository.findByUsuarioUsername(usuario.getUsername());
                if (medico != null) {
                    jwtBuilder.withClaim("medicoId", medico.getId());
                }
            }

            return jwtBuilder.sign(algoritmo);
        } catch (JWTCreationException exception){
            throw new RuntimeException("erro ao gerar token jwt", exception);
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    public String getSubject(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer(ISSUER) 
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }
}
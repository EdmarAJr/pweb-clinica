package br.ifba.edu.clinica.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfigurations {
    private SecurityFilter securityFilter;

    public SecurityConfigurations(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/pacientes/register").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/medicos/register").permitAll();
                    
                    req.requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/api-docs/**"
                    ).permitAll();

                    req.requestMatchers(HttpMethod.POST, "/auth/register").hasAuthority("ROLE_ADMIN");
                    req.requestMatchers(HttpMethod.POST, "/auth/validarMedico/{id}").hasAuthority("ROLE_ADMIN");
                    req.requestMatchers("/auth/validarMedico").hasAuthority("ROLE_ADMIN");

                    req.requestMatchers(HttpMethod.GET, "/pacientes/buscarPorNome").hasAuthority("ROLE_ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/pacientes/listar").hasAuthority("ROLE_ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/pacientes/{id}").hasAuthority("ROLE_ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/pacientes").hasAnyAuthority("ROLE_ADMIN", "ROLE_PACIENTE");
                    req.requestMatchers(HttpMethod.GET, "/pacientes/{id}").hasAnyAuthority("ROLE_ADMIN", "ROLE_PACIENTE");

                    req.requestMatchers(HttpMethod.GET, "/medicos/listar").hasAnyAuthority("ROLE_ADMIN", "ROLE_PACIENTE", "ROLE_MEDICO");
                    req.requestMatchers(HttpMethod.GET, "/medicos/buscarPorNome").hasAnyAuthority("ROLE_ADMIN", "ROLE_PACIENTE");
                    req.requestMatchers(HttpMethod.DELETE, "/medicos/{id}").hasAuthority("ROLE_ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/medicos").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO");
                    req.requestMatchers(HttpMethod.GET, "/medicos/{id}").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO");

                    req.requestMatchers("/consultas/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PACIENTE", "ROLE_MEDICO");

                    req.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint()))
                .build();

    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(403);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token JWT não informado ou inválido.\"}");
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://127.0.0.1:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
package br.ifba.edu.clinica.controllers;

import br.ifba.edu.clinica.dtos.LoginDTO;
import br.ifba.edu.clinica.dtos.TokenDTO;
import br.ifba.edu.clinica.dtos.UsuarioDTO;
import br.ifba.edu.clinica.dtos.MedicoDTO;
import br.ifba.edu.clinica.entities.Usuario;
import br.ifba.edu.clinica.services.JWTokenService;
import br.ifba.edu.clinica.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class UsuarioController {
    private AuthenticationManager manager;
    private UsuarioService usuarioservice;
    private JWTokenService tokenService;

    public UsuarioController(AuthenticationManager manager, UsuarioService usuarioservice, JWTokenService tokenService) {
        this.manager = manager;
        this.usuarioservice = usuarioservice;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> efetuarLogin(@RequestBody @Valid LoginDTO dadosLogin) {
        var dto = new UsernamePasswordAuthenticationToken(dadosLogin.username(), dadosLogin.password());
        var authentication = manager.authenticate(dto);
        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        return ResponseEntity.ok(new TokenDTO(tokenJWT));
    }

    @Operation(summary = "Cadastrar usuário", description = "Cadastra um novo novo usuário no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @ApiResponse(responseCode = "201", description = "Paciente cadastrado com sucesso")
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<UsuarioDTO> cadastrarUsuario(@RequestBody @Valid LoginDTO dadosLogin) {
        var usuario=usuarioservice.cadastrarUsuario(dadosLogin);
        return ResponseEntity.status(201).body(usuario);
    }

    @Operation(summary = "Deletar usuário", description = "Deleta um usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<UsuarioDTO> deletarUsuario(@PathVariable Long id) {
        var usuarioDeletado=usuarioservice.apagarUsuario(id);
        if (usuarioDeletado != null) {
            return ResponseEntity.ok(usuarioDeletado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários disponíveis")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários paginada (pode ser vazia)"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/listar")
    public ResponseEntity<Page<UsuarioDTO>> listarUsuarios(
            @ParameterObject
            @Parameter(description = "Parâmetros de paginação e ordenação")
            @PageableDefault(size = 10, page = 0, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(this.usuarioservice.getAllUsuarios(pageable));
    }

}
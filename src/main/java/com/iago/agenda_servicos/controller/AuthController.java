package com.iago.agenda_servicos.controller;

import com.iago.agenda_servicos.dto.AuthRequestDTO;
import com.iago.agenda_servicos.dto.AuthResponseDTO;
import com.iago.agenda_servicos.dto.UsuarioRequestDTO;
import com.iago.agenda_servicos.dto.UsuarioResponseDTO;
import com.iago.agenda_servicos.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "1. Autenticação & Registro", description = "Endpoints para login e registro de novos clientes")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        try {
            AuthResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            // BadCredentialsException, UsernameNotFoundException, etc.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponseDTO> register(@Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO novoUsuario = authService.registrarCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }
}

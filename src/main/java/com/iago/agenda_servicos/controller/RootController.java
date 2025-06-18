package com.iago.agenda_servicos.controller;

import com.iago.agenda_servicos.dto.UsuarioResponseDTO;
import com.iago.agenda_servicos.service.interfaces.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/root")
@Tag(name = "5. Super Admin", description = "Endpoints de gerenciamento para o SUPER_ADMIN")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Validated
public class RootController {

    private final UsuarioService usuarioService;

    public RootController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Lista todos os usuários do sistema.
     */
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Promove um usuário ao papel OWNER.
     */
    @PostMapping("/usuarios/{id}/promover-owner")
    public ResponseEntity<UsuarioResponseDTO> promoverOwner(@PathVariable Long id) {
        UsuarioResponseDTO atualizado = usuarioService.promoverParaOwner(id);
        return ResponseEntity.ok(atualizado);
    }
}

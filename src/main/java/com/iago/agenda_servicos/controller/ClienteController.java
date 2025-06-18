package com.iago.agenda_servicos.controller;

import com.iago.agenda_servicos.dto.AgendamentoDTO;
import com.iago.agenda_servicos.service.interfaces.AgendamentoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cliente")
@Tag(name = "2. Cliente", description = "Endpoints para o cliente gerenciar seus agendamentos")
@PreAuthorize("hasRole('CLIENTE')")
public class ClienteController {

    private final AgendamentoService agendamentoService;

    public ClienteController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping("/agendamentos")
    public ResponseEntity<AgendamentoDTO> agendar(@Valid @RequestBody AgendamentoDTO dto, Authentication authentication) {
        AgendamentoDTO agendamentoCriado = agendamentoService.criar(dto, authentication.getName());
        return new ResponseEntity<>(agendamentoCriado, HttpStatus.CREATED);
    }

    @DeleteMapping("/agendamentos/{id}")
    public ResponseEntity<Void> cancelarAgendamento(@PathVariable Long id, Authentication authentication) {
        agendamentoService.cancelar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/agendamentos")
    public ResponseEntity<List<AgendamentoDTO>> meusAgendamentos(Authentication authentication) {
        List<AgendamentoDTO> lista = agendamentoService.listarPorCliente(authentication.getName());
        return ResponseEntity.ok(lista);
    }
}

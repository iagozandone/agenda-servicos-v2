package com.iago.agenda_servicos.controller;

import com.iago.agenda_servicos.dto.*;
import com.iago.agenda_servicos.model.StatusAgendamento;
import com.iago.agenda_servicos.service.interfaces.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "4. Admin (Owner & Recepcionista)", description = "Endpoints de gerenciamento para donos e recepcionistas")
@PreAuthorize("hasRole('OWNER') or hasRole('RECEPCIONISTA')")
public class AdminController {

    private final BarbeariaService barbeariaService;
    private final ServicoService servicoService;
    private final UsuarioService usuarioService;
    private final AgendamentoService agendamentoService;
    private final RelatorioService relatorioService;

    public AdminController(
            BarbeariaService barbeariaService,
            ServicoService servicoService,
            UsuarioService usuarioService,
            AgendamentoService agendamentoService,
            RelatorioService relatorioService) {
        this.barbeariaService = barbeariaService;
        this.servicoService = servicoService;
        this.usuarioService = usuarioService;
        this.agendamentoService = agendamentoService;
        this.relatorioService = relatorioService;
    }

    // === OWNER ===

    @PostMapping("/barbearias")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BarbeariaDTO> criarBarbearia(
            @Valid @RequestBody BarbeariaDTO dto,
            Authentication auth) {
        var criado = barbeariaService.criar(dto, auth.getName());
        return new ResponseEntity<>(criado, HttpStatus.CREATED);
    }

    @PutMapping("/barbearias/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BarbeariaDTO> atualizarBarbearia(
            @PathVariable Long id,
            @Valid @RequestBody BarbeariaDTO dto) {
        var atualizado = barbeariaService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @PostMapping("/barbearias/{barbeariaId}/colaboradores")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UsuarioResponseDTO> criarColaborador(
            @PathVariable Long barbeariaId,
            @Valid @RequestBody UsuarioRequestDTO request) {
        var criado = usuarioService.criarColaborador(request, barbeariaId);
        return new ResponseEntity<>(criado, HttpStatus.CREATED);
    }

    @PostMapping("/servicos")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ServicoDTO> criarServico(
            @Valid @RequestBody ServicoDTO dto) {
        var criado = servicoService.criar(dto);
        return new ResponseEntity<>(criado, HttpStatus.CREATED);
    }

    @PutMapping("/servicos/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ServicoDTO> atualizarServico(
            @PathVariable Long id,
            @Valid @RequestBody ServicoDTO dto) {
        var atualizado = servicoService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    // === OWNER & RECEPCIONISTA ===

    @PatchMapping("/agendamentos/{id}/status")
    public ResponseEntity<AgendamentoDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusAgendamento status) {
        var atualizado = agendamentoService.atualizarStatus(id, status);
        return ResponseEntity.ok(atualizado);
    }

    @GetMapping("/barbearias/{barbeariaId}/agendamentos")
    public ResponseEntity<List<AgendamentoDTO>> listarAgendamentos(
            @PathVariable Long barbeariaId) {
        var lista = agendamentoService.listarPorBarbearia(barbeariaId);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/barbearias/{barbeariaId}/relatorios")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RelatorioDTO> relatorioDono(
            @PathVariable Long barbeariaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        var rel = relatorioService.gerarRelatorioDono(barbeariaId, inicio, fim);
        return ResponseEntity.ok(rel);
    }
}

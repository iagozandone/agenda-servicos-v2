package com.iago.agenda_servicos.controller;

import com.iago.agenda_servicos.dto.AgendamentoDTO;
import com.iago.agenda_servicos.dto.RelatorioDTO;
import com.iago.agenda_servicos.exception.ResourceNotFoundException;
import com.iago.agenda_servicos.model.Usuario;
import com.iago.agenda_servicos.model.StatusAgendamento;
import com.iago.agenda_servicos.repository.UsuarioRepository;
import com.iago.agenda_servicos.service.interfaces.AgendamentoService;
import com.iago.agenda_servicos.service.interfaces.RelatorioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/profissional")
@Tag(name = "3. Profissional", description = "Endpoints para o profissional visualizar sua agenda e relatórios")
@PreAuthorize("hasRole('PROFISSIONAL')")
@Validated
public class ProfissionalController {

    private final AgendamentoService agendamentoService;
    private final RelatorioService relatorioService;
    private final UsuarioRepository usuarioRepository;

    public ProfissionalController(AgendamentoService agendamentoService,
                                  RelatorioService relatorioService,
                                  UsuarioRepository usuarioRepository) {
        this.agendamentoService = agendamentoService;
        this.relatorioService = relatorioService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * 1. Visualizar agenda própria
     */
    @GetMapping("/agenda")
    public ResponseEntity<List<AgendamentoDTO>> minhaAgenda(Authentication authentication) {
        String email = authentication.getName();
        List<AgendamentoDTO> agenda = agendamentoService.listarPorProfissional(email);
        return ResponseEntity.ok(agenda);
    }

    /**
     * 2. Gerar relatório de atendimentos e comissões
     */
    @GetMapping("/relatorios")
    public ResponseEntity<RelatorioDTO> meuRelatorio(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        // Busca o usuário (profissional) pelo email e extrai o ID
        Usuario prof = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", authentication.getName()));
        Long profissionalId = prof.getId();

        RelatorioDTO relatorio = relatorioService.gerarRelatorioProfissional(
                profissionalId, dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }
}

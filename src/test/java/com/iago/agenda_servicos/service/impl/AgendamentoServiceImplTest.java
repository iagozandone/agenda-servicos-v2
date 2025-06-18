package com.iago.agenda_servicos.service.impl;

import com.iago.agenda_servicos.dto.AgendamentoDTO;
import com.iago.agenda_servicos.exception.AgendamentoInvalidoException;
import com.iago.agenda_servicos.model.*;
import com.iago.agenda_servicos.repository.AgendamentoRepository;
import com.iago.agenda_servicos.repository.ServicoRepository;
import com.iago.agenda_servicos.repository.UsuarioRepository;
import com.iago.agenda_servicos.service.interfaces.ComissaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Teste de Unidade para a classe AgendamentoServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class AgendamentoServiceImplTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ServicoRepository servicoRepository;
    @Mock
    private ComissaoService comissaoService;

    @InjectMocks
    private AgendamentoServiceImpl agendamentoService;

    private Usuario cliente;
    private Usuario profissional;
    private Barbearia barbearia;
    private Servico servico;
    private AgendamentoDTO agendamentoDTO;

    @BeforeEach
    void setUp() {
        cliente = Usuario.builder().id(1L).email("cliente@email.com").nomeCompleto("Cliente Teste").build();
        profissional = Usuario.builder().id(2L).email("prof@email.com").nomeCompleto("Profissional Teste").build();
        barbearia = Barbearia.builder().id(1L).nome("Barbearia Top").horaAbertura(LocalTime.of(9, 0)).horaFechamento(LocalTime.of(18, 0)).build();
        servico = Servico.builder().id(1L).nome("Corte de Cabelo").duracaoMinutos(30).precoBase(new BigDecimal("50.00")).barbearia(barbearia).build();

        agendamentoDTO = new AgendamentoDTO();
        agendamentoDTO.setServicoId(servico.getId());
        agendamentoDTO.setProfissionalId(profissional.getId());
    }

    @Test
    void criar_DeveRetornarAgendamentoDTO_QuandoDadosValidos() {
        // Arrange
        agendamentoDTO.setDataHora(LocalDateTime.now().plusDays(1).withHour(10));
        when(usuarioRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.of(cliente));
        when(servicoRepository.findById(servico.getId())).thenReturn(Optional.of(servico));
        when(usuarioRepository.findById(profissional.getId())).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.existeConflitoDeHorario(anyLong(), any(), any(), anyLong())).thenReturn(false);
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AgendamentoDTO resultado = agendamentoService.criar(agendamentoDTO, cliente.getEmail());

        // Assert
        assertNotNull(resultado);
        assertEquals(servico.getId(), resultado.getServicoId());
        verify(agendamentoRepository, times(1)).save(any(Agendamento.class));
    }

    @Test
    void criar_DeveLancarExcecao_QuandoForaDoHorarioDeFuncionamento() {
        // Arrange
        agendamentoDTO.setDataHora(LocalDateTime.now().plusDays(1).withHour(20));
        when(usuarioRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.of(cliente));
        when(servicoRepository.findById(servico.getId())).thenReturn(Optional.of(servico));
        when(usuarioRepository.findById(profissional.getId())).thenReturn(Optional.of(profissional));

        // Act & Assert
        var exception = assertThrows(AgendamentoInvalidoException.class, () -> agendamentoService.criar(agendamentoDTO, cliente.getEmail()));
        assertEquals("Fora do horário de funcionamento da barbearia.", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void atualizarStatus_DeveChamarComissaoService_QuandoStatusForRealizado() {
        // Arrange
        Agendamento agendamentoExistente = Agendamento.builder()
                .id(1L)
                .status(StatusAgendamento.AGENDADO)
                .profissional(profissional)
                .servico(servico)
                .build();

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoExistente));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoExistente);

        // Act
        agendamentoService.atualizarStatus(1L, StatusAgendamento.REALIZADO);

        // Assert
        verify(comissaoService, times(1)).gerarComissao(agendamentoExistente);
        assertEquals(StatusAgendamento.REALIZADO, agendamentoExistente.getStatus());
    }
}

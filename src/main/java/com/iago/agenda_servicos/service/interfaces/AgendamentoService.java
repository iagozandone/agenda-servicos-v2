package com.iago.agenda_servicos.service.interfaces;

import com.iago.agenda_servicos.dto.AgendamentoDTO;
import com.iago.agenda_servicos.model.StatusAgendamento;

import java.util.List;

public interface AgendamentoService {

    /**
     * Cria um novo agendamento para o cliente identificado pelo email.
     */
    AgendamentoDTO criar(AgendamentoDTO agendamentoDTO, String clienteEmail);

    /**
     * Atualiza o status de um agendamento (usado por OWNER, RECEPCIONISTA e PROFISSIONAL).
     */
    AgendamentoDTO atualizarStatus(Long agendamentoId, StatusAgendamento novoStatus);

    /**
     * Busca um único agendamento pelo seu ID.
     */
    AgendamentoDTO buscarPorId(Long id);

    /**
     * Lista todos os agendamentos do cliente (identificado pelo email do JWT).
     */
    List<AgendamentoDTO> listarPorCliente(String clienteEmail);

    /**
     * Lista todos os agendamentos do profissional (identificado pelo email do JWT).
     */
    List<AgendamentoDTO> listarPorProfissional(String profissionalEmail);

    /**
     * Lista todos os agendamentos de uma barbearia (para OWNER e RECEPCIONISTA).
     */
    List<AgendamentoDTO> listarPorBarbearia(Long barbeariaId);

    /**
     * Cancela um agendamento de um cliente, garantindo que este email foi o que criou.
     */
    void cancelar(Long agendamentoId, String clienteEmail);
}

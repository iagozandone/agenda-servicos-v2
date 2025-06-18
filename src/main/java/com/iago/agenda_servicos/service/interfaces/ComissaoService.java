package com.iago.agenda_servicos.service.interfaces;

import com.iago.agenda_servicos.model.Agendamento;

public interface ComissaoService {
    /**
     * Gera e salva um registro de comissão para um agendamento realizado.
     * Este método é chamado internamente pelo AgendamentoService.
     * @param agendamento O agendamento que foi concluído.
     */
    void gerarComissao(Agendamento agendamento);
}

package com.iago.agenda_servicos.dto;

import com.iago.agenda_servicos.model.StatusAgendamento;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RelatorioAgendamentoStatusDTO {
    private StatusAgendamento status;
    private Long quantidade;
}

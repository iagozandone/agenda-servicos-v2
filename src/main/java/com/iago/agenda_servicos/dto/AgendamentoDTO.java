package com.iago.agenda_servicos.dto;

import com.iago.agenda_servicos.model.Agendamento;
import com.iago.agenda_servicos.model.StatusAgendamento;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendamentoDTO {

    private Long id;

    @NotNull(message = "O ID do serviço é obrigatório.")
    private Long servicoId;

    @NotNull(message = "O ID do profissional é obrigatório.")
    private Long profissionalId;

    private Long clienteId; // Preenchido pelo backend a partir do token

    private String clienteNome;

    private String servicoNome;

    private String profissionalNome;

    @NotNull(message = "A data e hora são obrigatórias.")
    @Future(message = "A data do agendamento deve ser no futuro.")
    private LocalDateTime dataHora;

    private StatusAgendamento status;

    public AgendamentoDTO(Agendamento agendamento) {
        this.id = agendamento.getId();
        this.dataHora = agendamento.getDataHora();
        this.status = agendamento.getStatus();

        if (agendamento.getCliente() != null) {
            this.clienteId = agendamento.getCliente().getId();
            this.clienteNome = agendamento.getCliente().getNomeCompleto();
        }
        if (agendamento.getServico() != null) {
            this.servicoId = agendamento.getServico().getId();
            this.servicoNome = agendamento.getServico().getNome();
        }
        if (agendamento.getProfissional() != null) {
            this.profissionalId = agendamento.getProfissional().getId();
            this.profissionalNome = agendamento.getProfissional().getNomeCompleto();
        }
    }
}

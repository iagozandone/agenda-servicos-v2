package com.iago.agenda_servicos.dto;

import com.iago.agenda_servicos.model.Comissao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ComissaoDTO {

    private Long id;
    private Long agendamentoId;
    private Long profissionalId;
    private String profissionalNome;
    private LocalDate data;
    private BigDecimal percentual;
    private BigDecimal valor;

    public ComissaoDTO(Comissao comissao) {
        this.id = comissao.getId();
        this.data = comissao.getData();
        this.percentual = comissao.getPercentual();
        this.valor = comissao.getValor();

        if (comissao.getAgendamento() != null) {
            this.agendamentoId = comissao.getAgendamento().getId();
        }
        if (comissao.getProfissional() != null) {
            this.profissionalId = comissao.getProfissional().getId();
            this.profissionalNome = comissao.getProfissional().getNomeCompleto();
        }
    }
}

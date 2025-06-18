package com.iago.agenda_servicos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class RelatorioDTO {

    private BigDecimal faturamentoTotal;
    private BigDecimal comissoesPagas;
    private BigDecimal lucroLiquido;
    private Long totalAtendimentos;

    public RelatorioDTO(BigDecimal faturamentoTotal, BigDecimal comissoesPagas, Long totalAtendimentos) {
        this.faturamentoTotal = faturamentoTotal != null ? faturamentoTotal : BigDecimal.ZERO;
        this.comissoesPagas = comissoesPagas != null ? comissoesPagas : BigDecimal.ZERO;
        this.totalAtendimentos = totalAtendimentos != null ? totalAtendimentos : 0L;

        if (this.faturamentoTotal != null && this.comissoesPagas != null) {
            this.lucroLiquido = this.faturamentoTotal.subtract(this.comissoesPagas);
        } else {
            this.lucroLiquido = BigDecimal.ZERO;
        }
    }
}

package com.iago.agenda_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RelatorioReceitaDTO {
    private String nomeBarbearia;
    private BigDecimal receitaTotal;
}

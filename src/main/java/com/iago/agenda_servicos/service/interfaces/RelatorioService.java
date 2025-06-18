package com.iago.agenda_servicos.service.interfaces;

import com.iago.agenda_servicos.dto.RelatorioDTO;
import java.time.LocalDate;

public interface RelatorioService {
    /**
     * Gera um relatório financeiro para o dono de uma barbearia.
     * @param barbeariaId ID da barbearia.
     * @param dataInicio Data de início do período do relatório.
     * @param dataFim Data de fim do período do relatório.
     * @return DTO com os dados do relatório.
     */
    RelatorioDTO gerarRelatorioDono(Long barbeariaId, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Gera um relatório de comissões para um profissional.
     * @param profissionalId ID do profissional.
     * @param dataInicio Data de início do período.
     * @param dataFim Data de fim do período.
     * @return DTO com os dados do relatório.
     */
    RelatorioDTO gerarRelatorioProfissional(Long profissionalId, LocalDate dataInicio, LocalDate dataFim);
}

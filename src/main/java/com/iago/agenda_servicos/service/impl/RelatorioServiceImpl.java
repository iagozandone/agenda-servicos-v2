package com.iago.agenda_servicos.service.impl;

import com.iago.agenda_servicos.dto.RelatorioDTO;
import com.iago.agenda_servicos.repository.AgendamentoRepository;
import com.iago.agenda_servicos.service.interfaces.RelatorioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class RelatorioServiceImpl implements RelatorioService {

    private final AgendamentoRepository agendamentoRepository;

    public RelatorioServiceImpl(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public RelatorioDTO gerarRelatorioDono(Long barbeariaId, LocalDate dataInicio, LocalDate dataFim) {
        return agendamentoRepository.gerarRelatorioParaDono(
                barbeariaId,
                dataInicio.atStartOfDay(),
                dataFim.atTime(LocalTime.MAX)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RelatorioDTO gerarRelatorioProfissional(Long profissionalId, LocalDate dataInicio, LocalDate dataFim) {
        return agendamentoRepository.gerarRelatorioParaProfissional(
                profissionalId,
                dataInicio.atStartOfDay(),
                dataFim.atTime(LocalTime.MAX)
        );
    }
}

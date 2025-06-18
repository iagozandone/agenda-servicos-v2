package com.iago.agenda_servicos.service.impl;

import com.iago.agenda_servicos.model.Agendamento;
import com.iago.agenda_servicos.model.Comissao;
import com.iago.agenda_servicos.model.Usuario;
import com.iago.agenda_servicos.repository.ComissaoRepository;
import com.iago.agenda_servicos.service.interfaces.ComissaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class ComissaoServiceImpl implements ComissaoService {

    private final ComissaoRepository comissaoRepository;

    public ComissaoServiceImpl(ComissaoRepository comissaoRepository) {
        this.comissaoRepository = comissaoRepository;
    }

    @Override
    @Transactional
    public void gerarComissao(Agendamento agendamento) {
        Usuario profissional = agendamento.getProfissional();
        if (profissional == null || profissional.getPercentualComissao() == null) {
            // Se não há profissional ou ele não tem comissão definida, não faz nada.
            return;
        }

        BigDecimal precoServico = agendamento.getServico().getPrecoBase();
        BigDecimal percentual = profissional.getPercentualComissao();
        BigDecimal valorComissao = precoServico.multiply(percentual).setScale(2, RoundingMode.HALF_UP);

        Comissao comissao = Comissao.builder()
                .agendamento(agendamento)
                .profissional(profissional)
                .data(LocalDate.now())
                .percentual(percentual)
                .valor(valorComissao)
                .build();

        comissaoRepository.save(comissao);
    }
}

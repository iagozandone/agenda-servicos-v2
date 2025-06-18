package com.iago.agenda_servicos.service.impl;

import com.iago.agenda_servicos.dto.ServicoDTO;
import com.iago.agenda_servicos.exception.ResourceNotFoundException;
import com.iago.agenda_servicos.model.Barbearia;
import com.iago.agenda_servicos.model.Servico;
import com.iago.agenda_servicos.repository.BarbeariaRepository;
import com.iago.agenda_servicos.repository.ServicoRepository;
import com.iago.agenda_servicos.service.interfaces.ServicoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicoServiceImpl implements ServicoService {

    private final ServicoRepository servicoRepository;
    private final BarbeariaRepository barbeariaRepository;

    public ServicoServiceImpl(ServicoRepository servicoRepository, BarbeariaRepository barbeariaRepository) {
        this.servicoRepository = servicoRepository;
        this.barbeariaRepository = barbeariaRepository;
    }

    @Override
    @Transactional
    public ServicoDTO criar(ServicoDTO dto) {
        Barbearia barbearia = barbeariaRepository.findById(dto.getBarbeariaId())
                .orElseThrow(() -> new ResourceNotFoundException("Barbearia", "id", dto.getBarbeariaId()));

        Servico servico = Servico.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .duracaoMinutos(dto.getDuracaoMinutos())
                .precoBase(dto.getPrecoBase())
                .ativo(dto.getAtivo())
                .barbearia(barbearia)
                .build();

        return new ServicoDTO(servicoRepository.save(servico));
    }

    @Override
    @Transactional
    public ServicoDTO atualizar(Long id, ServicoDTO dto) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço", "id", id));

        servico.setNome(dto.getNome());
        servico.setDescricao(dto.getDescricao());
        servico.setDuracaoMinutos(dto.getDuracaoMinutos());
        servico.setPrecoBase(dto.getPrecoBase());
        servico.setAtivo(dto.getAtivo());

        return new ServicoDTO(servicoRepository.save(servico));
    }

    @Override
    @Transactional(readOnly = true)
    public ServicoDTO buscarPorId(Long id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço", "id", id));
        return new ServicoDTO(servico);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicoDTO> listarPorBarbearia(Long barbeariaId) {
        if (!barbeariaRepository.existsById(barbeariaId)) {
            throw new ResourceNotFoundException("Barbearia", "id", barbeariaId);
        }
        return servicoRepository.findByBarbeariaAndAtivoTrue(
                barbeariaRepository.getReferenceById(barbeariaId)
        ).stream().map(ServicoDTO::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Serviço", "id", id);
        }
        servicoRepository.deleteById(id);
    }
}

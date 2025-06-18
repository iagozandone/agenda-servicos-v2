package com.iago.agenda_servicos.service.impl;

import com.iago.agenda_servicos.dto.BarbeariaDTO;
import com.iago.agenda_servicos.exception.ResourceNotFoundException;
import com.iago.agenda_servicos.model.Barbearia;
import com.iago.agenda_servicos.model.Usuario;
import com.iago.agenda_servicos.repository.BarbeariaRepository;
import com.iago.agenda_servicos.repository.UsuarioRepository;
import com.iago.agenda_servicos.service.interfaces.BarbeariaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarbeariaServiceImpl implements BarbeariaService {

    private final BarbeariaRepository barbeariaRepository;
    private final UsuarioRepository usuarioRepository;

    public BarbeariaServiceImpl(BarbeariaRepository barbeariaRepository, UsuarioRepository usuarioRepository) {
        this.barbeariaRepository = barbeariaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public BarbeariaDTO criar(BarbeariaDTO dto, String ownerEmail) {
        Usuario owner = usuarioRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário (Owner)", "email", ownerEmail));

        Barbearia barbearia = Barbearia.builder()
                .nome(dto.getNome())
                .endereco(dto.getEndereco())
                .logoUrl(dto.getLogoUrl())
                .horaAbertura(dto.getHoraAbertura())
                .horaFechamento(dto.getHoraFechamento())
                .owner(owner)
                .build();

        return new BarbeariaDTO(barbeariaRepository.save(barbearia));
    }

    @Override
    @Transactional
    public BarbeariaDTO atualizar(Long id, BarbeariaDTO dto) {
        Barbearia barbearia = barbeariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barbearia", "id", id));

        barbearia.setNome(dto.getNome());
        barbearia.setEndereco(dto.getEndereco());
        barbearia.setLogoUrl(dto.getLogoUrl());
        barbearia.setHoraAbertura(dto.getHoraAbertura());
        barbearia.setHoraFechamento(dto.getHoraFechamento());

        return new BarbeariaDTO(barbeariaRepository.save(barbearia));
    }

    @Override
    @Transactional(readOnly = true)
    public BarbeariaDTO buscarPorId(Long id) {
        Barbearia barbearia = barbeariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barbearia", "id", id));
        return new BarbeariaDTO(barbearia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BarbeariaDTO> listarPorDono(String ownerEmail) {
        Usuario owner = usuarioRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário (Owner)", "email", ownerEmail));

        return barbeariaRepository.findByOwner(owner).stream()
                .map(BarbeariaDTO::new)
                .collect(Collectors.toList());
    }
}

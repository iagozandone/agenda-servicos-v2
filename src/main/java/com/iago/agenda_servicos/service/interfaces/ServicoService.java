package com.iago.agenda_servicos.service.interfaces;

import com.iago.agenda_servicos.dto.ServicoDTO;
import java.util.List;

public interface ServicoService {
    ServicoDTO criar(ServicoDTO servicoDTO);
    ServicoDTO atualizar(Long id, ServicoDTO servicoDTO);
    ServicoDTO buscarPorId(Long id);
    List<ServicoDTO> listarPorBarbearia(Long barbeariaId);
    void deletar(Long id);
}

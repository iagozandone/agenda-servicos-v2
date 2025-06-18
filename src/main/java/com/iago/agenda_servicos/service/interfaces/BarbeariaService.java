package com.iago.agenda_servicos.service.interfaces;

import com.iago.agenda_servicos.dto.BarbeariaDTO;
import java.util.List;

public interface BarbeariaService {
    /**
     * Cria uma nova barbearia.
     * @param barbeariaDTO DTO com os dados da barbearia.
     * @param ownerEmail Email do usuário (OWNER) que está criando a barbearia.
     * @return DTO da barbearia criada.
     */
    BarbeariaDTO criar(BarbeariaDTO barbeariaDTO, String ownerEmail);

    /**
     * Atualiza os dados de uma barbearia existente.
     * @param id ID da barbearia a ser atualizada.
     * @param barbeariaDTO DTO com os novos dados.
     * @return DTO da barbearia atualizada.
     */
    BarbeariaDTO atualizar(Long id, BarbeariaDTO barbeariaDTO);

    /**
     * Busca uma barbearia pelo seu ID.
     * @param id ID da barbearia.
     * @return DTO da barbearia encontrada.
     */
    BarbeariaDTO buscarPorId(Long id);

    /**
     * Lista todas as barbearias de um determinado dono.
     * @param ownerEmail Email do dono.
     * @return Lista de DTOs de barbearias.
     */
    List<BarbeariaDTO> listarPorDono(String ownerEmail);
}

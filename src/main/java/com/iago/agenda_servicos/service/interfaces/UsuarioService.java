package com.iago.agenda_servicos.service.interfaces;

import com.iago.agenda_servicos.dto.UsuarioRequestDTO;
import com.iago.agenda_servicos.dto.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {

    /**
     * Cria um novo colaborador (PROFISSIONAL ou RECEPCIONISTA) para uma barbearia.
     *
     * @param usuarioDTO DTO com os dados de entrada do colaborador.
     * @param barbeariaId ID da barbearia à qual o colaborador será vinculado.
     * @return DTO de saída com os dados do colaborador criado.
     */
    UsuarioResponseDTO criarColaborador(UsuarioRequestDTO usuarioDTO, Long barbeariaId);

    /**
     * Promove um usuário para o perfil de OWNER. Apenas um SUPER_ADMIN pode fazer isso.
     *
     * @param usuarioId ID do usuário a ser promovido.
     * @return DTO de saída com os dados do usuário atualizado.
     */
    UsuarioResponseDTO promoverParaOwner(Long usuarioId);

    /**
     * Busca um usuário pelo seu ID.
     *
     * @param id ID do usuário.
     * @return DTO de saída com os dados do usuário encontrado.
     */
    UsuarioResponseDTO buscarPorId(Long id);

    /**
     * Lista todos os usuários do sistema.
     *
     * @return Lista de DTOs de saída de todos os usuários.
     */
    List<UsuarioResponseDTO> listarTodos();
}

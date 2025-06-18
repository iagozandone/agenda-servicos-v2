package com.iago.agenda_servicos.service.impl;

import com.iago.agenda_servicos.dto.UsuarioRequestDTO;
import com.iago.agenda_servicos.dto.UsuarioResponseDTO;
import com.iago.agenda_servicos.exception.ResourceNotFoundException;
import com.iago.agenda_servicos.model.Barbearia;
import com.iago.agenda_servicos.model.Role;
import com.iago.agenda_servicos.model.RoleName;
import com.iago.agenda_servicos.model.Usuario;
import com.iago.agenda_servicos.repository.BarbeariaRepository;
import com.iago.agenda_servicos.repository.RoleRepository;
import com.iago.agenda_servicos.repository.UsuarioRepository;
import com.iago.agenda_servicos.service.interfaces.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final BarbeariaRepository barbeariaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            BarbeariaRepository barbeariaRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.barbeariaRepository = barbeariaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioResponseDTO criarColaborador(UsuarioRequestDTO dto, Long barbeariaId) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Erro: E-mail já está em uso!");
        }

        Barbearia barbearia = barbeariaRepository.findById(barbeariaId)
                .orElseThrow(() -> new ResourceNotFoundException("Barbearia", "id", barbeariaId));

        // Mapeia e valida roles
        Set<Role> roles = dto.getRoles().stream()
                .map(roleStr -> {
                    RoleName rn = RoleName.valueOf(roleStr);
                    if (rn != RoleName.ROLE_PROFISSIONAL && rn != RoleName.ROLE_RECEPCIONISTA) {
                        throw new IllegalArgumentException("Colaborador só pode ter o perfil PROFISSIONAL ou RECEPCIONISTA.");
                    }
                    return roleRepository.findByNome(rn)
                            .orElseThrow(() -> new RuntimeException("Erro: Perfil " + rn + " não encontrado."));
                })
                .collect(Collectors.toSet());

        Usuario novo = Usuario.builder()
                .nomeCompleto(dto.getNomeCompleto())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .endereco(dto.getEndereco())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .roles(roles)
                .barbearia(barbearia)
                .percentualComissao(dto.getPercentualComissao())
                .build();

        Usuario salvo = usuarioRepository.save(novo);
        return new UsuarioResponseDTO(salvo);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO promoverParaOwner(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", usuarioId));

        Role ownerRole = roleRepository.findByNome(RoleName.ROLE_OWNER)
                .orElseThrow(() -> new RuntimeException("Erro: Perfil OWNER não encontrado."));

        usuario.getRoles().add(ownerRole);
        usuario.setBarbearia(null); // desvincula da barbearia

        Usuario updated = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        return new UsuarioResponseDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }
}

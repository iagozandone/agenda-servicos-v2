package com.iago.agenda_servicos.security;

import com.iago.agenda_servicos.model.Usuario;
import com.iago.agenda_servicos.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getSenha(),
                u.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority(r.getNome().name()))
                        .collect(Collectors.toList())
        );
    }
}

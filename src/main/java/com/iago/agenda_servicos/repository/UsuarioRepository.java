package com.iago.agenda_servicos.repository;

import com.iago.agenda_servicos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /**
     * Busca um usuário pelo seu endereço de e-mail.
     * @param email O e-mail do usuário.
     * @return um Optional contendo o Usuário se encontrado.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se um usuário com o e-mail especificado já existe.
     * @param email O e-mail a ser verificado.
     * @return true se o e-mail existir, false caso contrário.
     */
    boolean existsByEmail(String email);
}

package com.iago.agenda_servicos.repository;

import com.iago.agenda_servicos.model.Role;
import com.iago.agenda_servicos.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Busca uma Role pelo seu nome (enum).
     * @param nome O enum RoleName a ser buscado.
     * @return um Optional contendo a Role se encontrada.
     */
    Optional<Role> findByNome(RoleName nome);
}

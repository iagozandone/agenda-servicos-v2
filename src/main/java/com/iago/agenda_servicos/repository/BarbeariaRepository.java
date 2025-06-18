package com.iago.agenda_servicos.repository;

import com.iago.agenda_servicos.model.Barbearia;
import com.iago.agenda_servicos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BarbeariaRepository extends JpaRepository<Barbearia, Long> {
    /**
     * Encontra todas as barbearias pertencentes a um determinado dono.
     * @param owner O objeto Usuário do dono.
     * @return Uma lista de barbearias.
     */
    List<Barbearia> findByOwner(Usuario owner);
}

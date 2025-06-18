package com.iago.agenda_servicos.repository;

import com.iago.agenda_servicos.model.Barbearia;
import com.iago.agenda_servicos.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    /**
     * Encontra todos os serviços de uma barbearia que estão marcados como ativos.
     * @param barbearia A entidade Barbearia.
     * @return Uma lista de serviços ativos.
     */
    List<Servico> findByBarbeariaAndAtivoTrue(Barbearia barbearia);
}

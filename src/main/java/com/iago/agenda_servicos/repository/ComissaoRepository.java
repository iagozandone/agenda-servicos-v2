package com.iago.agenda_servicos.repository;

import com.iago.agenda_servicos.model.Comissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComissaoRepository extends JpaRepository<Comissao, Long> {
    // Métodos de busca para relatórios podem ser adicionados aqui no futuro.
    // Ex: List<Comissao> findByProfissionalIdAndDataBetween(Long profissionalId, LocalDate inicio, LocalDate fim);
}

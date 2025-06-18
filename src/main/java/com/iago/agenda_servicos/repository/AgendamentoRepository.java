package com.iago.agenda_servicos.repository;

import com.iago.agenda_servicos.dto.RelatorioDTO;
import com.iago.agenda_servicos.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("SELECT COUNT(a) > 0 FROM Agendamento a " +
            "WHERE a.profissional.id = :profissionalId " +
            "AND a.id <> :agendamentoId " +
            "AND a.status <> 'CANCELADO' " +
            "AND a.dataHora < :fimNovo " +
            "AND FUNCTION('ADDTIME', a.dataHora, FUNCTION('SEC_TO_TIME', a.servico.duracaoMinutos * 60)) > :inicioNovo")
    boolean existeConflitoDeHorario(
            @Param("profissionalId") Long profissionalId,
            @Param("inicioNovo") LocalDateTime inicioNovo,
            @Param("fimNovo") LocalDateTime fimNovo,
            @Param("agendamentoId") Long agendamentoId);

    @Query("SELECT new com.iago.agenda_servicos.dto.RelatorioDTO(" +
            "   SUM(CASE WHEN a.status = 'REALIZADO' THEN s.precoBase END), " +
            "   SUM(CASE WHEN a.status = 'REALIZADO' THEN c.valor END), " +
            "   COUNT(CASE WHEN a.status = 'REALIZADO' THEN 1 END)) " +
            "FROM Agendamento a " +
            "LEFT JOIN a.servico s " +
            "LEFT JOIN Comissao c ON c.agendamento = a " +
            "WHERE a.barbearia.id = :barbeariaId " +
            "AND a.dataHora BETWEEN :dataInicio AND :dataFim")
    RelatorioDTO gerarRelatorioParaDono(
            @Param("barbeariaId") Long barbeariaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT new com.iago.agenda_servicos.dto.RelatorioDTO(" +
            "   SUM(CASE WHEN a.status = 'REALIZADO' THEN s.precoBase END), " +
            "   SUM(CASE WHEN a.status = 'REALIZADO' THEN c.valor END), " +
            "   COUNT(CASE WHEN a.status = 'REALIZADO' THEN 1 END)) " +
            "FROM Agendamento a " +
            "LEFT JOIN a.servico s " +
            "LEFT JOIN Comissao c ON c.agendamento = a " +
            "WHERE a.profissional.id = :profissionalId " +
            "AND a.dataHora BETWEEN :dataInicio AND :dataFim")
    RelatorioDTO gerarRelatorioParaProfissional(
            @Param("profissionalId") Long profissionalId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);


    List<Agendamento> findByClienteId(Long clienteId);
    List<Agendamento> findByProfissionalId(Long profissionalId);
    List<Agendamento> findByBarbeariaId(Long barbeariaId);
}

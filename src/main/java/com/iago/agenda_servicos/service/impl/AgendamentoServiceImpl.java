package com.iago.agenda_servicos.service.impl;

import com.iago.agenda_servicos.dto.AgendamentoDTO;
import com.iago.agenda_servicos.exception.AgendamentoInvalidoException;
import com.iago.agenda_servicos.exception.ResourceNotFoundException;
import com.iago.agenda_servicos.model.Agendamento;
import com.iago.agenda_servicos.model.StatusAgendamento;
import com.iago.agenda_servicos.model.Servico;
import com.iago.agenda_servicos.model.Usuario;
import com.iago.agenda_servicos.repository.AgendamentoRepository;
import com.iago.agenda_servicos.repository.ServicoRepository;
import com.iago.agenda_servicos.repository.UsuarioRepository;
import com.iago.agenda_servicos.service.interfaces.AgendamentoService;
import com.iago.agenda_servicos.service.interfaces.ComissaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendamentoServiceImpl implements AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicoRepository servicoRepository;
    private final ComissaoService comissaoService;

    public AgendamentoServiceImpl(AgendamentoRepository agendamentoRepository,
                                  UsuarioRepository usuarioRepository,
                                  ServicoRepository servicoRepository,
                                  ComissaoService comissaoService) {
        this.agendamentoRepository = agendamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicoRepository = servicoRepository;
        this.comissaoService = comissaoService;
    }

    @Override
    @Transactional
    public AgendamentoDTO criar(AgendamentoDTO dto, String clienteEmail) {
        // 1. Buscar entidades necessárias (ou lançar 404)
        Usuario cliente = usuarioRepository.findByEmail(clienteEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", clienteEmail));
        Servico servico = servicoRepository.findById(dto.getServicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço", "id", dto.getServicoId()));
        Usuario profissional = usuarioRepository.findById(dto.getProfissionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", dto.getProfissionalId()));

        LocalDateTime dataHora = dto.getDataHora();
        LocalTime hora = dataHora.toLocalTime();

        // 2. Verificar horário de funcionamento
        LocalTime abertura = servico.getBarbearia().getHoraAbertura();
        LocalTime fechamento = servico.getBarbearia().getHoraFechamento();
        if (hora.isBefore(abertura) || hora.isAfter(fechamento)) {
            throw new AgendamentoInvalidoException("Fora do horário de funcionamento da barbearia.");
        }

        // 3. Verificar conflito de horário
        LocalDateTime fim = dataHora.plusMinutes(servico.getDuracaoMinutos());
        boolean conflito = agendamentoRepository.existeConflitoDeHorario(
                profissional.getId(),
                dataHora,
                fim,
                -1L // -1 para novos agendamentos
        );
        if (conflito) {
            throw new AgendamentoInvalidoException("Conflito de horário para este profissional.");
        }

        // 4. Construir e salvar entidade
        Agendamento agendamento = Agendamento.builder()
                .cliente(cliente)
                .profissional(profissional)
                .servico(servico)
                .barbearia(servico.getBarbearia())
                .dataHora(dataHora)
                .status(StatusAgendamento.AGENDADO)
                .build();

        Agendamento salvo = agendamentoRepository.save(agendamento);

        // 5. Retornar DTO
        return new AgendamentoDTO(salvo);
    }

    @Override
    @Transactional
    public AgendamentoDTO atualizarStatus(Long agendamentoId, StatusAgendamento novoStatus) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", "id", agendamentoId));

        agendamento.setStatus(novoStatus);
        Agendamento atualizado = agendamentoRepository.save(agendamento);

        if (novoStatus == StatusAgendamento.REALIZADO) {
            comissaoService.gerarComissao(atualizado);
        }

        return new AgendamentoDTO(atualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public AgendamentoDTO buscarPorId(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", "id", id));
        return new AgendamentoDTO(agendamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgendamentoDTO> listarPorCliente(String clienteEmail) {
        Usuario cliente = usuarioRepository.findByEmail(clienteEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", clienteEmail));
        return agendamentoRepository.findByClienteId(cliente.getId()).stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgendamentoDTO> listarPorProfissional(String profissionalEmail) {
        Usuario profissional = usuarioRepository.findByEmail(profissionalEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", profissionalEmail));
        return agendamentoRepository.findByProfissionalId(profissional.getId()).stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgendamentoDTO> listarPorBarbearia(Long barbeariaId) {
        return agendamentoRepository.findByBarbeariaId(barbeariaId).stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelar(Long agendamentoId, String clienteEmail) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", "id", agendamentoId));
        if (!agendamento.getCliente().getEmail().equals(clienteEmail)) {
            throw new AgendamentoInvalidoException("Você não pode cancelar este agendamento.");
        }
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
    }
}

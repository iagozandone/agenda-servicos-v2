package com.iago.agenda_servicos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iago.agenda_servicos.dto.AgendamentoDTO;
import com.iago.agenda_servicos.dto.AuthRequestDTO;
import com.iago.agenda_servicos.dto.AuthResponseDTO;
import com.iago.agenda_servicos.model.*;
import com.iago.agenda_servicos.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BarbeariaRepository barbeariaRepository;
    @Autowired
    private ServicoRepository servicoRepository;
    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String clienteToken;
    private Long profissionalId;
    private Long servicoId;

    @BeforeEach
    void setUp() throws Exception {
        // Limpeza inicial
        agendamentoRepository.deleteAll();
        servicoRepository.deleteAll();
        barbeariaRepository.deleteAll();
        usuarioRepository.deleteAll();

        // 1. Criar perfis necessários
        Role ownerRole = roleRepository.findByNome(RoleName.ROLE_OWNER).orElseThrow();
        Role clienteRole = roleRepository.findByNome(RoleName.ROLE_CLIENTE).orElseThrow();
        Role profissionalRole = roleRepository.findByNome(RoleName.ROLE_PROFISSIONAL).orElseThrow();

        // 2. Criar um DONO para a barbearia
        Usuario owner = usuarioRepository.save(Usuario.builder()
                .nomeCompleto("Dono Teste").email("dono.teste@email.com")
                .senha(passwordEncoder.encode("senha123")).telefone("123")
                .roles(Set.of(ownerRole)).build());

        // 3. Criar a BARBEARIA
        Barbearia barbearia = barbeariaRepository.save(Barbearia.builder()
                .nome("Barbearia Teste").endereco("Rua Teste")
                .horaAbertura(LocalTime.of(8, 0)).horaFechamento(LocalTime.of(20, 0))
                .owner(owner).build());

        // 4. Criar um PROFISSIONAL e um SERVIÇO na barbearia
        Usuario profissional = usuarioRepository.save(Usuario.builder()
                .nomeCompleto("Profissional Teste").email("prof.teste@email.com")
                .senha(passwordEncoder.encode("senha123")).telefone("456")
                .roles(Set.of(profissionalRole)).barbearia(barbearia).build());
        this.profissionalId = profissional.getId();

        Servico servico = servicoRepository.save(Servico.builder()
                .nome("Corte Teste").duracaoMinutos(30).precoBase(new BigDecimal("40.00"))
                .ativo(true).barbearia(barbearia).build());
        this.servicoId = servico.getId();

        // 5. Criar e autenticar o CLIENTE para obter o token
        Usuario cliente = usuarioRepository.save(Usuario.builder()
                .nomeCompleto("Cliente Teste").email("cliente.teste@email.com")
                .senha(passwordEncoder.encode("senha123")).telefone("789")
                .roles(Set.of(clienteRole)).build());

        AuthRequestDTO loginRequest = new AuthRequestDTO(cliente.getEmail(), "senha123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        this.clienteToken = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponseDTO.class).getToken();
    }

    @Test
    void agendar_DeveRetornarStatus201_QuandoClienteAutenticado() throws Exception {
        // ARRANGE
        AgendamentoDTO agendamentoDTO = AgendamentoDTO.builder()
                .servicoId(this.servicoId)
                .profissionalId(this.profissionalId)
                .dataHora(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0))
                .build();

        // ACT & ASSERT
        mockMvc.perform(post("/api/cliente/agendamentos")
                        .header("Authorization", "Bearer " + this.clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(agendamentoDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void agendar_DeveRetornarStatus401_QuandoNaoAutenticado() throws Exception {
        // ARRANGE
        AgendamentoDTO agendamentoDTO = AgendamentoDTO.builder()
                .servicoId(this.servicoId)
                .profissionalId(this.profissionalId)
                .dataHora(LocalDateTime.now().plusDays(2).withHour(11).withMinute(0))
                .build();

        // ACT & ASSERT
        mockMvc.perform(post("/api/cliente/agendamentos") // Sem cabeçalho de autorização
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(agendamentoDTO)))
                .andExpect(status().isUnauthorized()); // Espera 401 Unauthorized
    }

    @Test
    void cancelarAgendamento_DeveRetornarStatus204_QuandoClienteCancelaProprioAgendamento() throws Exception {
        // ARRANGE: Primeiro, criamos um agendamento para poder cancelar
        AgendamentoDTO agendamentoParaCancelar = AgendamentoDTO.builder()
                .servicoId(this.servicoId)
                .profissionalId(this.profissionalId)
                .dataHora(LocalDateTime.now().plusDays(3).withHour(14).withMinute(0))
                .build();

        MvcResult result = mockMvc.perform(post("/api/cliente/agendamentos")
                        .header("Authorization", "Bearer " + this.clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(agendamentoParaCancelar)))
                .andExpect(status().isCreated())
                .andReturn();

        AgendamentoDTO agendamentoCriado = objectMapper.readValue(result.getResponse().getContentAsString(), AgendamentoDTO.class);
        Long idParaCancelar = agendamentoCriado.getId();

        // ACT & ASSERT
        mockMvc.perform(delete("/api/cliente/agendamentos/" + idParaCancelar)
                        .header("Authorization", "Bearer " + this.clienteToken))
                .andExpect(status().isNoContent()); // Espera 204 No Content
    }
}

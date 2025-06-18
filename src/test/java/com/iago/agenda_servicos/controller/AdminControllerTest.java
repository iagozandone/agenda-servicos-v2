package com.iago.agenda_servicos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iago.agenda_servicos.dto.AuthRequestDTO;
import com.iago.agenda_servicos.dto.AuthResponseDTO;
import com.iago.agenda_servicos.dto.BarbeariaDTO;
import com.iago.agenda_servicos.dto.UsuarioRequestDTO;
import com.iago.agenda_servicos.model.Role;
import com.iago.agenda_servicos.model.RoleName;
import com.iago.agenda_servicos.model.Usuario;
import com.iago.agenda_servicos.repository.RoleRepository;
import com.iago.agenda_servicos.repository.UsuarioRepository;
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

import java.time.LocalTime;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String ownerToken;
    private Long ownerId;

    @BeforeEach
    void setUp() throws Exception {
        // Garante que o banco esteja limpo
        usuarioRepository.deleteAll();

        // 1. Criar e salvar um usuário com o perfil de OWNER
        Role ownerRole = roleRepository.findByNome(RoleName.ROLE_OWNER)
                .orElseThrow(() -> new RuntimeException("Perfil OWNER não encontrado"));

        Usuario owner = Usuario.builder()
                .nomeCompleto("Dono da Barbearia")
                .email("owner@email.com")
                .senha(passwordEncoder.encode("senha123"))
                .telefone("11999999999")
                .roles(Set.of(ownerRole))
                .build();
        Usuario savedOwner = usuarioRepository.saveAndFlush(owner);
        ownerId = savedOwner.getId();

        // 2. Fazer login com esse usuário para obter um token JWT válido
        AuthRequestDTO loginRequest = new AuthRequestDTO("owner@email.com", "senha123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AuthResponseDTO authResponse = objectMapper.readValue(responseBody, AuthResponseDTO.class);
        this.ownerToken = authResponse.getToken();
    }

    @Test
    void criarBarbearia_DeveRetornarStatus201_QuandoUsuarioForOwner() throws Exception {
        // ARRANGE
        BarbeariaDTO barbeariaDTO = BarbeariaDTO.builder()
                .nome("Barbearia do Dono")
                .endereco("Rua dos Testes, 123")
                .horaAbertura(LocalTime.of(9, 0))
                .horaFechamento(LocalTime.of(19, 0))
                .build();

        // ACT & ASSERT
        mockMvc.perform(post("/api/admin/barbearias")
                        .header("Authorization", "Bearer " + this.ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(barbeariaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Barbearia do Dono"))
                .andExpect(jsonPath("$.ownerId").value(this.ownerId));
    }

    @Test
    void criarBarbearia_DeveRetornarStatus403_QuandoUsuarioNaoForOwner() throws Exception {
        // ARRANGE
        // Primeiro, obtemos um token de um usuário comum (cliente)
        UsuarioRequestDTO clienteDTO = new UsuarioRequestDTO();
        clienteDTO.setEmail("cliente.comum@email.com");
        clienteDTO.setNomeCompleto("Cliente Comum");
        clienteDTO.setSenha("senha123");
        clienteDTO.setTelefone("11888888888");

        mockMvc.perform(post("/auth/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)));

        AuthRequestDTO loginRequest = new AuthRequestDTO("cliente.comum@email.com", "senha123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        String clienteToken = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponseDTO.class).getToken();

        // CORREÇÃO: Preenche o DTO com dados válidos para passar na validação
        BarbeariaDTO barbeariaDTO = BarbeariaDTO.builder()
                .nome("Barbearia de Teste Invalido")
                .endereco("Rua de Teste, 404")
                .build();

        // ACT & ASSERT
        // Usamos o token do CLIENTE para tentar acessar um endpoint de OWNER
        mockMvc.perform(post("/api/admin/barbearias")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(barbeariaDTO)))
                // A resposta esperada é 403 Forbidden, pois o cliente não tem permissão
                .andExpect(status().isForbidden());
    }
}

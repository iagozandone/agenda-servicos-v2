package com.iago.agenda_servicos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iago.agenda_servicos.dto.AuthRequestDTO;
import com.iago.agenda_servicos.dto.UsuarioRequestDTO;
import com.iago.agenda_servicos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioRequestDTO usuarioParaRegistrar;

    @BeforeEach
    void setUp() {
        // A anotação @Transactional já garante que o banco estará limpo, mas
        // deleteAll() é uma boa prática para tornar a intenção explícita.
        usuarioRepository.deleteAll();

        usuarioParaRegistrar = new UsuarioRequestDTO();
        usuarioParaRegistrar.setEmail("integracao@email.com");
        usuarioParaRegistrar.setNomeCompleto("Usuario Integracao");
        usuarioParaRegistrar.setSenha("senhaForte123");
        usuarioParaRegistrar.setTelefone("987654321");
    }

    private void registrarUsuarioParaTeste() throws Exception {
        mockMvc.perform(post("/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioParaRegistrar)))
                .andExpect(status().isCreated());
    }

    @Test
    void registrar_DeveRetornarStatus201ECriarUsuario() throws Exception {
        mockMvc.perform(post("/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioParaRegistrar)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("integracao@email.com"));
    }

    @Test
    void login_DeveRetornarStatus200EToken_QuandoCredenciaisCorretas() throws Exception {
        registrarUsuarioParaTeste();

        AuthRequestDTO loginRequest = new AuthRequestDTO(usuarioParaRegistrar.getEmail(), usuarioParaRegistrar.getSenha());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    void login_DeveRetornarStatus403_QuandoSenhaIncorreta() throws Exception {
        registrarUsuarioParaTeste();
        AuthRequestDTO loginRequest = new AuthRequestDTO(usuarioParaRegistrar.getEmail(), "senhaErrada");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }
}

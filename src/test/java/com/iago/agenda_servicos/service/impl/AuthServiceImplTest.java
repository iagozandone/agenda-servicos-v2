package com.iago.agenda_servicos.service.impl;

import com.iago.agenda_servicos.dto.AuthRequestDTO;
import com.iago.agenda_servicos.dto.AuthResponseDTO;
import com.iago.agenda_servicos.dto.UsuarioRequestDTO;
import com.iago.agenda_servicos.dto.UsuarioResponseDTO;
import com.iago.agenda_servicos.model.Role;
import com.iago.agenda_servicos.model.RoleName;
import com.iago.agenda_servicos.model.Usuario;
import com.iago.agenda_servicos.repository.RoleRepository;
import com.iago.agenda_servicos.repository.UsuarioRepository;
import com.iago.agenda_servicos.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Teste de Unidade para a classe AuthServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private UsuarioRequestDTO usuarioRequestDTO;
    private Usuario usuario;
    private Role clienteRole;

    @BeforeEach
    void setUp() {
        clienteRole = new Role(RoleName.ROLE_CLIENTE);

        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setEmail("teste@email.com");
        usuarioRequestDTO.setNomeCompleto("Usuario Teste");
        usuarioRequestDTO.setSenha("senha123");
        usuarioRequestDTO.setTelefone("123456789");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@email.com");
        usuario.setNomeCompleto("Usuario Teste");
        usuario.setSenha("senhaCodificada");
        usuario.setRoles(Set.of(clienteRole));
    }

    @Test
    void registrarCliente_DeveRetornarUsuarioResponseDTO_QuandoSucesso() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByNome(RoleName.ROLE_CLIENTE)).thenReturn(Optional.of(clienteRole));
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCodificada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        UsuarioResponseDTO resultado = authService.registrarCliente(usuarioRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("teste@email.com", resultado.getEmail());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void registrarCliente_DeveLancarExcecao_QuandoEmailJaExiste() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.registrarCliente(usuarioRequestDTO));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void login_DeveRetornarAuthResponseDTO_QuandoCredenciaisCorretas() {
        // Arrange
        AuthRequestDTO requestDTO = new AuthRequestDTO("teste@email.com", "senha123");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(jwtTokenProvider.generateToken(any())).thenReturn("fake.jwt.token");

        // Act
        AuthResponseDTO response = authService.login(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("fake.jwt.token", response.getToken());
        assertEquals("Usuario Teste", response.getNomeCompleto());
        assertTrue(response.getRoles().contains("ROLE_CLIENTE"));
    }
}

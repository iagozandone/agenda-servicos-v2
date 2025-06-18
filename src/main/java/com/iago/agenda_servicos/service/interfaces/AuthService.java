// src/main/java/com/iago/agenda_servicos/service/interfaces/AuthService.java
package com.iago.agenda_servicos.service.interfaces;

import com.iago.agenda_servicos.dto.AuthRequestDTO;
import com.iago.agenda_servicos.dto.AuthResponseDTO;
import com.iago.agenda_servicos.dto.UsuarioRequestDTO;
import com.iago.agenda_servicos.dto.UsuarioResponseDTO;

public interface AuthService {
    AuthResponseDTO login(AuthRequestDTO authRequestDTO);
    UsuarioResponseDTO registrarCliente(UsuarioRequestDTO usuarioRequestDTO);
}

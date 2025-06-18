package com.iago.agenda_servicos.dto;

import java.util.Set;

public class AuthResponseDTO {
    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String nomeCompleto;
    private Set<String> roles;

    public AuthResponseDTO(String token, Long id, String nomeCompleto, Set<String> roles) {
        this.token = token;
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.roles = roles;
    }

    // getters (Jackson vai serializar em "token", "tipo", "id", etc)
    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }

    public Long getId() {
        return id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public Set<String> getRoles() {
        return roles;
    }
}

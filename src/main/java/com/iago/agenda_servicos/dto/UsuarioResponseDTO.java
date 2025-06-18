package com.iago.agenda_servicos.dto;

import com.iago.agenda_servicos.model.Usuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String email;
    private String telefone;
    private String endereco;
    private Set<String> roles;
    private BigDecimal percentualComissao;

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nomeCompleto = usuario.getNomeCompleto();
        this.email = usuario.getEmail();
        this.telefone = usuario.getTelefone();
        this.endereco = usuario.getEndereco();
        this.roles = usuario.getRoles().stream()
                .map(role -> role.getNome().name())
                .collect(Collectors.toSet());
        this.percentualComissao = usuario.getPercentualComissao();
    }
}

package com.iago.agenda_servicos.dto;

import com.iago.agenda_servicos.model.Barbearia;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class BarbeariaDTO {

    private Long id;

    @NotBlank(message = "O nome do estabelecimento é obrigatório.")
    private String nome;

    @NotBlank(message = "O endereço é obrigatório.")
    private String endereco;

    private String logoUrl;

    private LocalTime horaAbertura;

    private LocalTime horaFechamento;

    private Long ownerId;

    private String ownerNome;

    public BarbeariaDTO(Barbearia barbearia) {
        this.id = barbearia.getId();
        this.nome = barbearia.getNome();
        this.endereco = barbearia.getEndereco();
        this.logoUrl = barbearia.getLogoUrl();
        this.horaAbertura = barbearia.getHoraAbertura();
        this.horaFechamento = barbearia.getHoraFechamento();
        if (barbearia.getOwner() != null) {
            this.ownerId = barbearia.getOwner().getId();
            this.ownerNome = barbearia.getOwner().getNomeCompleto();
        }
    }
}

package com.iago.agenda_servicos.dto;

import com.iago.agenda_servicos.model.Servico;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ServicoDTO {

    private Long id;

    @NotBlank(message = "O nome do serviço é obrigatório.")
    private String nome;

    private String descricao;

    @NotNull(message = "A duração em minutos é obrigatória.")
    @Min(value = 1, message = "A duração deve ser de no mínimo 1 minuto.")
    private Integer duracaoMinutos;

    @NotNull(message = "O preço base é obrigatório.")
    @Min(value = 0, message = "O preço não pode ser negativo.")
    private BigDecimal precoBase;

    @NotNull
    private Boolean ativo = true;

    @NotNull(message = "O ID da barbearia é obrigatório.")
    private Long barbeariaId;

    public ServicoDTO(Servico servico) {
        this.id = servico.getId();
        this.nome = servico.getNome();
        this.descricao = servico.getDescricao();
        this.duracaoMinutos = servico.getDuracaoMinutos();
        this.precoBase = servico.getPrecoBase();
        this.ativo = servico.getAtivo();
        if (servico.getBarbearia() != null) {
            this.barbeariaId = servico.getBarbearia().getId();
        }
    }
}

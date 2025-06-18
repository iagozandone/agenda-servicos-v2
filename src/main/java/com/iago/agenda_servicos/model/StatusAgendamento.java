package com.iago.agenda_servicos.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatusAgendamento {
    AGENDADO,
    CANCELADO,
    REALIZADO,
    NAO_COMPARECEU;

    @JsonCreator
    public static StatusAgendamento fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (StatusAgendamento status : StatusAgendamento.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status desconhecido: " + value);
    }
}

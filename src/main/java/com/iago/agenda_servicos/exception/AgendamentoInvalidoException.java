package com.iago.agenda_servicos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção para regras de negócio específicas de agendamento que foram violadas.
 * (Ex: agendar no passado, conflito de horário, etc).
 * Retorna o status HTTP 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AgendamentoInvalidoException extends RuntimeException {

    public AgendamentoInvalidoException(String message) {
        super(message);
    }
}

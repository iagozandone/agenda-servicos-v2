package com.iago.agenda_servicos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção para tentativas de acesso não autorizadas a recursos ou ações.
 * (Ex: um cliente tentando cancelar o agendamento de outro).
 * Retorna o status HTTP 403 (Forbidden).
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}

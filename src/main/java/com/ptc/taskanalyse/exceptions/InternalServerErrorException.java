package com.ptc.taskanalyse.exceptions;

import org.omg.CORBA.INTERNAL;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by asasmaz on 22/05/2017.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException() {}

    public InternalServerErrorException(Exception base) {
        this.initCause(base);
    }
}

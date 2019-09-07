package com.manu.domoback.exceptions;

public class BusinessRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessRuntimeException(final String message) {
        super(message);
    }
}

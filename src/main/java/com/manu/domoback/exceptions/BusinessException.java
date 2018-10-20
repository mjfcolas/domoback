package com.manu.domoback.exceptions;

public class BusinessException extends Exception {

    private static final long serialVersionUID = 1L;

    public BusinessException(final String message) {
        super(message);
    }
}

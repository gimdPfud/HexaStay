package com.sixthsense.hexastay.util.exception;

public class UnauthorizedOrderAccessException extends RuntimeException{

    public UnauthorizedOrderAccessException(String message) {
        super(message);
    }
}

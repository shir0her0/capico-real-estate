package com.capico.auth.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Email or password is incorrect");
    }
}

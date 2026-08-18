package com.vickey.skillforge.exception;


public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(String message)
    {
        super(message);
    }
}

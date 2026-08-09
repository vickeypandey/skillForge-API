package com.vickey.skillforge.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String message)
    {
        super(message);
    }
}

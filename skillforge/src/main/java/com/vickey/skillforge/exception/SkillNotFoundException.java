package com.vickey.skillforge.exception;

public class SkillNotFoundException extends RuntimeException {
    public SkillNotFoundException(String message){
        super(message);
    }
}

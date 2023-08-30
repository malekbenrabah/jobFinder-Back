package com.example.springsecurity.exception;

public class PasswordExistsEception  extends RuntimeException{
    public PasswordExistsEception( String message) {
        super(message);
    }

}

package com.example.springsecurity.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class InvalidOldPasswordException extends RuntimeException {

    public InvalidOldPasswordException( String message) {
        super(message);
    }


}

package com.example.springsecurity.service.JobAlert;

public class UserAlreadyAppliedException extends RuntimeException {
    public UserAlreadyAppliedException(String message) {
        super(message);
    }
}
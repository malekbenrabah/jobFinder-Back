package com.example.springsecurity.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutenticationResponse {
    private String token; // token which will be sent back to the customer
    private boolean success;
    private String message;

}

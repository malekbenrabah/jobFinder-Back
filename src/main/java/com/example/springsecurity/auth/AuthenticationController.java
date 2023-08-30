package com.example.springsecurity.auth;

import com.example.springsecurity.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AutenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        try{
            return ResponseEntity.ok(authenticationService.register(request));

        }catch (DuplicateEmailException ex){
            return ResponseEntity.badRequest().body(AutenticationResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .build());
        }



    }

    @PostMapping("/authenticate")
    public ResponseEntity<AutenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/expiredJWT")
    public ResponseEntity<Boolean> checkTokenExpired(@NonNull HttpServletRequest request){
        // get the token from the authorization
        final String authHeader=request.getHeader("Authorization");

        final String jwt=authHeader.substring(7);

        boolean isExpired= jwtService.isTokenExpired(jwt);
        return ResponseEntity.ok(isExpired);
    }

}

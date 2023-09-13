package com.example.springsecurity.auth;

import com.example.springsecurity.config.JwtService;
import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.token.Token;
import com.example.springsecurity.token.TokenRepository;
import com.example.springsecurity.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final TokenRepository tokenRepository;

    public AutenticationResponse register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException("Email already exists !! ");
        }


        if(Role.COMPANY.equals(request.getRole())) {
            if (userRepository.existsByCompanyName(request.getCompanyName())) {
                throw new DuplicateCompanyNameException("Company Name already exists");
            }
        }

        var user= User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .created_at(LocalDateTime.now())
                .companyName(request.getCompanyName())
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        //logout additions
        saveUserToken(user, jwtToken);
        //end logout additions
        return AutenticationResponse
                .builder()
                .success(true)
                .token(jwtToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token= Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user){
        var validUserTokens= tokenRepository.findAllValidTokensByUser(user.getId());
        if(validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t->{
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public AutenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
           new UsernamePasswordAuthenticationToken(
                   request.getEmail(),
                   request.getPassword()
           )
        );
        //if email and password are correct => generate a token and send it back
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user,jwtToken);
        return AutenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }


}

package com.example.springsecurity.controller;

import com.example.springsecurity.auth.AutenticationResponse;
import com.example.springsecurity.config.JwtService;
import com.example.springsecurity.dto.UserDTO;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.*;
import com.example.springsecurity.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import com.example.springsecurity.repository.UserRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;






    @GetMapping("/userInfo")
    public UserDTO getUserInfo(@NonNull HttpServletRequest request) {
        return userService.getUserInfo(request);
    }


    @PutMapping("/updatePhoto")
    public UserDTO updatePhoto(@RequestParam(value="photo",required = false) MultipartFile photo
                                            ,@NonNull HttpServletRequest request ) throws IOException {

        return userService.updatePhoto(photo,request);
    }

    @PutMapping("/updateUser")
    public UserDTO updateUser(@NonNull HttpServletRequest request,
                              @RequestParam("user") String userJSON,
                              @RequestParam(value="photo",required = false) MultipartFile photo) throws IOException{

        // Convert the JSON string to a UserDTO object
        ObjectMapper objectMapper = new ObjectMapper();
        UserDTO userDTO = objectMapper.readValue(userJSON, UserDTO.class);

        return userService.updateUser(request, userDTO, photo);
    }


    @PutMapping("/updatePass")
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@NonNull HttpServletRequest request,
                                                                @RequestParam("oldPass")  String oldPass,
                                                                @RequestParam("newPass") String newPass) {
       // userService.updatePassword(request,oldPass,newPass);
        try{
            return ResponseEntity.ok(userService.updatePassword(request,oldPass,newPass));
        }catch (InvalidOldPasswordException ex){
            return ResponseEntity.badRequest().body(UpdatePasswordResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .build());

        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestParam("email") String email) throws MessagingException {

        try{
            return ResponseEntity.ok(userService.forgotPassword(email));
        }catch (UserNotFoundException ex){
            return ResponseEntity.badRequest().body(ForgotPasswordResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .build()
            );
        }
    }

    @PutMapping("/set-password")
    public ResponseEntity<ResetPasswordResponse> setPassword(@RequestParam("email") String email, @RequestHeader("newPassword") String newPassword) {


        try {
            return  ResponseEntity.ok(this.userService.setPassword(email,newPassword));

        }catch (PasswordExistsEception ex){
            return ResponseEntity.badRequest()
                    .body(ResetPasswordResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build()
                    );       }
    }


   /*
    @GetMapping("/test")
    public UserDetails getUseeer(@NonNull HttpServletRequest request){
        logger.debug("Entering getUseeer method");

        // get the token from the authorization
        final String authHeader=request.getHeader("Authorization");

        final String jwt=authHeader.substring(7);

        //get the user's email from the token
        final String userEmail= jwtService.extractUsername(jwt);
        logger.debug("JWT: {}", jwt);
        logger.debug("User email: {}", userEmail);

        UserDetails userDetails= userDetailsService.loadUserByUsername(userEmail);
        logger.debug("User details: {}", userDetails);

        logger.debug("Exiting getUseeer method");
        return userDetails;

    }

    */



}

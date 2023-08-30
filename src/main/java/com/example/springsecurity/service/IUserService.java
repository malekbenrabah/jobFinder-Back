package com.example.springsecurity.service;

import com.example.springsecurity.dto.UserDTO;
import com.example.springsecurity.exception.ForgotPasswordResponse;
import com.example.springsecurity.exception.ResetPasswordResponse;
import com.example.springsecurity.exception.UpdatePasswordResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUserService {

    public UserDTO getUserInfo(HttpServletRequest request);

    public UserDTO updatePhoto(MultipartFile photo,HttpServletRequest request)throws IOException;

    public UserDTO updateUser(HttpServletRequest request,UserDTO userDTO,MultipartFile photo) throws IOException;

    public UpdatePasswordResponse updatePassword(HttpServletRequest request, String oldPass, String newPass);

    public ForgotPasswordResponse forgotPassword(String email) throws MessagingException;


    public ResetPasswordResponse setPassword(String email, String newPassword);
}

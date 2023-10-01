package com.example.springsecurity.service;

import com.example.springsecurity.dto.UserDTO;
import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.Skill;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.ForgotPasswordResponse;
import com.example.springsecurity.exception.ResetPasswordResponse;
import com.example.springsecurity.exception.UpdatePasswordResponse;
import com.lowagie.text.DocumentException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IUserService {

    UserDTO getUserInfo(HttpServletRequest request);

    UserDTO updatePhoto(MultipartFile photo,HttpServletRequest request)throws IOException;

    UserDTO updateUser(HttpServletRequest request,UserDTO userDTO,MultipartFile photo) throws IOException;

    UserDTO updateUserInfo(HttpServletRequest request,UserDTO userDTO);
    UpdatePasswordResponse updatePassword(HttpServletRequest request, String oldPass, String newPass);

    ForgotPasswordResponse forgotPassword(String email) throws MessagingException;


    ResetPasswordResponse setPassword(String email, String newPassword);

    List<UserDTO> getAllUsers();

    boolean checkCompanyName(String companyName);

    boolean checkEmailUser(String userEmail);

    public User getUserByToken(HttpServletRequest request);

    List<UserDTO> getCompanies();

    List<UserDTO> getUsers();

    Integer nbUsers();

    public UserDTO getUserById(Integer id);

    public Role getUserRole(HttpServletRequest request);

    public void deleteUser(HttpServletRequest request,Integer id);

}

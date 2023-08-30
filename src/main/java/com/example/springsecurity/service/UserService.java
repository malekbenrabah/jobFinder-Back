package com.example.springsecurity.service;

import com.example.springsecurity.config.ApplicationConfig;
import com.example.springsecurity.config.JwtService;
import com.example.springsecurity.dto.UserDTO;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springsecurity.repository.UserRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
public class UserService implements IUserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private Environment env;

    @Autowired
    TemplateEngine templateEngine;





    @Override
    public UserDTO getUserInfo(HttpServletRequest request) {

        // get the token from the authorization
        final String authHeader=request.getHeader("Authorization");

        final String jwt=authHeader.substring(7);

        //get the user's email from the token
        final String userEmail= jwtService.extractUsername(jwt);

        User user=userRepository.findUserByEmail(userEmail);
        return UserDTO.fromEntityToDTO(user);
    }

    public User getUserByToken(HttpServletRequest request){
        final String authHeader=request.getHeader("Authorization");

        final String jwt=authHeader.substring(7);

        //get the user's email from the token
        final String userEmail= jwtService.extractUsername(jwt);

        User user=userRepository.findUserByEmail(userEmail);
        return user;
    }


    @Override
    public UserDTO updatePhoto(MultipartFile photo, HttpServletRequest request) throws IOException {

        User user= getUserByToken(request);

        user.setPhoto(storeImage(photo));
        userRepository.save(user);
        return UserDTO.fromEntityToDTO(user);

    }

    @Override
    public UserDTO updateUser(HttpServletRequest request, UserDTO userDTO, MultipartFile photo) throws IOException {
        User user= getUserByToken(request);
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setPhone(userDTO.getPhone());
        user.setPhoto(storeImage(photo));
        userRepository.save(user);
        return UserDTO.fromEntityToDTO(user);
    }




    public String storeImage(MultipartFile profileImage) throws IOException {
        //String imagePath = null;
        String angularImagePath = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(profileImage.getOriginalFilename());
            /*
            //getting current directory
            String currentDir = System.getProperty("user.dir");
            Path uploadDir = Paths.get(currentDir, "src", "main", "resources", "images");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            try (InputStream inputStream = profileImage.getInputStream()) {
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                imagePath = filePath.toAbsolutePath().toString();
            } catch (IOException ex) {
                throw new IOException("Could not store file " + fileName + ". Please try again!", ex);
            }*/

            // save image to angular project directory

            Path angularUploadDir = Paths.get("C:", "Users","Mohamed","Desktop","Malek","learning","ngZoroLearning", "src", "assets", "images");
            if (!Files.exists(angularUploadDir)) {
                Files.createDirectories(angularUploadDir);
            }
            try (InputStream inputStream = profileImage.getInputStream()) {
                Path angularFilePath = angularUploadDir.resolve(fileName);
                Files.copy(inputStream, angularFilePath, StandardCopyOption.REPLACE_EXISTING);
                angularImagePath = "../assets/images/" + fileName;
                //angularImagePath = angularFilePath.toAbsolutePath().toString();
            } catch (IOException ex) {
                throw new IOException("Could not store file " + fileName + ". Please try again!", ex);
            }



        }
        return angularImagePath;
    }


    @Override
    public UpdatePasswordResponse updatePassword(HttpServletRequest request, String oldPass, String newPass) {
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        User user= getUserByToken(request);

        boolean valid =passwordEncoder.matches(oldPass,user.getPassword());

        /*
        if(valid){
            user.setPassword(passwordEncoder.encode(newPass));
            userRepository.save(user);
        }else{
            throw new InvalidOldPasswordException("invalid old password");
        }


         */

        if(!valid){
            throw new InvalidOldPasswordException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(newPass));
        userRepository.save(user);

        return  UpdatePasswordResponse
                .builder()
                .success(true)
                .message("password user successfully updated")
                .build();
    }

    @Override
    public ForgotPasswordResponse forgotPassword(String email) throws MessagingException {
        System.out.println("begin fogot password");

        User user= userRepository.findUserByEmail(email);
        if(user==null){
            throw new UserNotFoundException("User not found");
        }

        //sending email
        sendResetPassEmail(email);

        return ForgotPasswordResponse.builder()
                .success(true)
                .message("Please check your email to set your password")
                .build();

    }

    public void sendResetPassEmail(String email) throws MessagingException {
        //sending the link email
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage);
        //setting email parameters
        String fromEmail= env.getProperty("spring.mail.username");
        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Reset Password");


        mimeMessageHelper.setText("""
                <div>
                <a href="http://localhost:4200/auth/pass-reset" target="_blank">click link to set password</a>
                </div>
                """.formatted(email),true);

        /*
        mimeMessageHelper.setText("""
                <div>
                <a href="http://localhost:8086/app/user/set-password?email=%s" target="_blank">click link to set password</a>
                </div>
                """.formatted(email),true);

         */

        //send email
        javaMailSender.send(mimeMessage);

    }

    @Override
    public ResetPasswordResponse setPassword(String email, String newPassword) {
        User user= userRepository.findUserByEmail(email);
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        String hashedNewPassword = passwordEncoder.encode(newPassword);

        if(passwordEncoder.matches(newPassword, user.getPassword())){
            throw  new PasswordExistsEception("password already exists");
        }
        user.setPassword(hashedNewPassword);
        userRepository.save(user);
        return ResetPasswordResponse.builder()
                .success(true)
                .message("New password set successfully")
                .build();
    }







}

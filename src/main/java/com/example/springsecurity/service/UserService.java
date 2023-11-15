package com.example.springsecurity.service;

import com.example.springsecurity.config.ApplicationConfig;
import com.example.springsecurity.config.JwtService;
import com.example.springsecurity.dto.*;
import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.*;
import com.example.springsecurity.service.Education.IEducationService;
import com.example.springsecurity.service.Experience.IExperienceService;
import com.example.springsecurity.service.Skills.ISkillsService;
import com.example.springsecurity.token.Token;
import com.example.springsecurity.token.TokenRepository;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.lowagie.text.DocumentException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springsecurity.repository.UserRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService implements IUserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private Environment env;






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

    @Override
    public UserDTO getUserInfoById(Integer id) {
        User user = userRepository.findById(id).get();
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
    public List<UserDTO> getCompanies() {
        List<User> users=userRepository.findByCompanyNameNotNull();

        return users.stream()
                .map(user -> new UserDTO().fromEntityToDTO(user))
                .collect(Collectors.toList());

    }

    @Override
    public List<UserDTO> getUsers() {

        List<User> users=userRepository.getUsers();
        return users.stream()
                .map(user -> new UserDTO().fromEntityToDTO(user))
                .collect(Collectors.toList());
    }

    @Override
    public Integer nbUsers() {
        return userRepository.getNbUsers();
    }

    @Override
    public UserDTO getUserById(Integer id) {
        User user= userRepository.findById(id).orElse(null);
        return UserDTO.fromEntityToDTO(user);
    }

    @Override
    public Role getUserRole(HttpServletRequest request) {
        User user= getUserByToken(request);
        return user.getRole();
    }

    @Override
    public void deleteUser(HttpServletRequest request,Integer id) {
        User user=userRepository.findById(id).orElse(null);
        List<Token> tokens=tokenRepository.findAllValidTokensByUser(id);
        for (Token jwt:tokens) {
            tokenRepository.delete(jwt);
        }
        userRepository.delete(user);
    }

    @Override
    public UserDTO updateUserCv(Integer id) {
        User user = userRepository.findById(id).get();
        user.setCv_created(true);
        userRepository.save(user);
        return UserDTO.fromEntityToDTO(user);

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
        user.setCompanyName(userDTO.getCompanyName());
        user.setAdresse(userDTO.getAdresse());
        user.setAboutMe(userDTO.getAboutMe());
        userRepository.save(user);
        return UserDTO.fromEntityToDTO(user);
    }

    @Override
    public UserDTO updateUserInfo(HttpServletRequest request, UserDTO userDTO) {
        User user= getUserByToken(request);
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setPhone(userDTO.getPhone());
        user.setCompanyName(userDTO.getCompanyName());
        user.setAdresse(userDTO.getAdresse());
        user.setAboutMe(userDTO.getAboutMe());
        userRepository.save(user);
        return UserDTO.fromEntityToDTO(user);
    }


    public String storeImage(MultipartFile profileImage) throws IOException {
        String imagePath = null;
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

            //saving into spring path
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
            }

            // save image to angular project directory

            Path angularUploadDir = Paths.get("C:", "Users","Mohamed","Desktop","Malek","internship","angular","jobFinder", "src", "assets", "images");
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

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users=userRepository.findAll();

        return users.stream()
                .map(user -> new UserDTO().fromEntityToDTO(user))
                .collect(Collectors.toList());

    }

    @Override
    public boolean checkCompanyName(String companyName) {
        System.out.println("check company name start");
        return this.userRepository.existsByCompanyName(companyName);

    }

    @Override
    public boolean checkEmailUser(String userEmail) {
        System.out.println("check user email start");
        return this.userRepository.existsByEmail(userEmail);
    }


    //PDF




    /*
    public String htmlToPdf(String processedHtml){
        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        try{
            PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
            DefaultFontProvider defaultFontProvider = new DefaultFontProvider(false, true,false);
            ConverterProperties converterProperties = new ConverterProperties();

            converterProperties.setFontProvider(defaultFontProvider);
            //convert Html into pdf
            HtmlConverter.convertToPdf(processedHtml,pdfWriter,converterProperties);

            FileOutputStream fileOutputStream = new FileOutputStream("C:/Users/Mohamed/Desktop/Malek/internship/angular/jobFinder/src/assets/files/cv.pdf");

            byteArrayOutputStream.writeTo(fileOutputStream);
            byteArrayOutputStream.close();

            byteArrayOutputStream.flush();
            fileOutputStream.close();
            return null;
        }catch (Exception ex){

        }
        return null;
    }

    */

    /*
    private String parseThymleafTemplate(HttpServletRequest request){
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);


        UserDTO user = getUserInfo(request);
        List<SkillDTO> skills = skillsService.getUserSkills(request);
        List<EducationDTO> educations= educationService.getUserEducation(request);
        List<ExperienceDTO> experiences= experienceService.getUserExperiences(request);


        Context context = new Context();
        context.setVariable("userInfo", user);
        context.setVariable("educations", educations);
        context.setVariable("skills", skills);
        context.setVariable("experiences", experiences);

        String htmlContent = templateEngine.process("pdf-template",context);



    }

     */






}

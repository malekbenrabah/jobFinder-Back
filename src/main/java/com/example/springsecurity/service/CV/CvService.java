package com.example.springsecurity.service.CV;

import com.example.springsecurity.dto.EducationDTO;
import com.example.springsecurity.dto.ExperienceDTO;
import com.example.springsecurity.dto.SkillDTO;
import com.example.springsecurity.dto.UserDTO;
import com.example.springsecurity.service.Education.IEducationService;
import com.example.springsecurity.service.Experience.IExperienceService;
import com.example.springsecurity.service.IUserService;
import com.example.springsecurity.service.Skills.ISkillsService;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
public class CvService implements ICvService{

    @Autowired
    IUserService userService;

    @Autowired
    IEducationService educationService;

    @Autowired
    ISkillsService skillsService;

    @Autowired
    IExperienceService experienceService;

    @Autowired
    TemplateEngine templateEngine;


    @Override
    public byte[] generatePdf(HttpServletRequest request) throws DocumentException, IOException, com.itextpdf.text.DocumentException {
        //getting the user's infos
        UserDTO user = userService.getUserInfo(request);
        List<SkillDTO> skills = skillsService.getUserSkills(request);
        List<EducationDTO> educations= educationService.getUserEducation(request);
        List<ExperienceDTO> experiences= experienceService.getUserExperiences(request);

        // create thymleaf context
        Context context = new Context();
        // add infos
        String userPhoto=user.getPhoto();
        String imageName = userPhoto.substring(userPhoto.lastIndexOf('/') + 1);
        context.setVariable("userPhoto", imageName);
        context.setVariable("userInfo", user);
        context.setVariable("educations", educations);
        context.setVariable("skills", skills);
        context.setVariable("experiences", experiences);

        //render thymleaf templare to html content
        String htmlContent = templateEngine.process("pdf-template",context);

        //create PDF document
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer= PdfWriter.getInstance(document,byteArrayOutputStream);
        document.open();

        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(htmlContent.getBytes()));

        document.close();

        // Save PDF to a specific location on the server
        String outputPath = "C:/Users/Mohamed/Desktop/Malek/internship/angular/jobFinder/src/assets/files/cv.pdf";
        Path outputPathPath = Path.of(outputPath);
        Files.write(outputPathPath, byteArrayOutputStream.toByteArray(), StandardOpenOption.CREATE);

        return byteArrayOutputStream.toByteArray();


    }


}

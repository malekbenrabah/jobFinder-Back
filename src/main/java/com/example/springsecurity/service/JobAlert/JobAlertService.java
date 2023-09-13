package com.example.springsecurity.service.JobAlert;

import com.example.springsecurity.dto.JobAlertDTO;
import com.example.springsecurity.dto.JobDTO;
import com.example.springsecurity.dto.SkillDTO;
import com.example.springsecurity.entity.Job;
import com.example.springsecurity.entity.JobAlert;
import com.example.springsecurity.entity.Skill;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.repository.JobAlertRepository;
import com.example.springsecurity.repository.JobRepository;
import com.example.springsecurity.repository.SkillRepository;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.service.IUserService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobAlertService implements IJobAlertService{
    @Autowired
    JobAlertRepository jobAlertRepository;

    @Autowired
    IUserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SkillRepository skillRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private Environment env;

    @Autowired
    TemplateEngine templateEngine;

    @Override
    public JobAlertDTO createJobAlert(HttpServletRequest request, JobAlert jobAlert) {
        User user = userService.getUserByToken(request);

        jobAlert.setSent(false);
        jobAlert.setCreated_at(LocalDateTime.now());
        jobAlert.setUser(user);


        List<Skill> savedSkills = new ArrayList<>();
        /*
        for (Skill skill : jobAlert.getSkills()) {
            // skill already exists in the database by skill name
            Skill existingSkill = skillRepository.findBySkill(skill.getSkill());
            if (existingSkill != null) {
                savedSkills.add(existingSkill); // use the existing skill
            } else {
                // skill doesn't exist => create and save a new skill
                Skill newSkill = new Skill();
                newSkill.setSkill(skill.getSkill());
                skillRepository.save(newSkill);
                savedSkills.add(newSkill);
            }
        }
        */

        for (Skill skill : jobAlert.getSkills()) {
            Skill newSkill = new Skill();
            newSkill.setSkill(skill.getSkill());
            skillRepository.save(newSkill);
            savedSkills.add(newSkill);
        }

        // Set the saved skills in the jobAlert
        jobAlert.setSkills(savedSkills);

        jobAlertRepository.save(jobAlert);

        return JobAlertDTO.fromEntityToDTO(jobAlert);
    }

    @Override
        public void deleteJobAlert(HttpServletRequest request, Integer id) {
            JobAlert jobAlert = jobAlertRepository.findById(id).get();
            List<Skill>jobAlertSkills=jobAlert.getSkills();

            jobAlertRepository.delete(jobAlert);

            for (Skill skill:jobAlertSkills) {
                skillRepository.delete(skill);
            }

        }

    @Override
    public JobAlertDTO updateJobAlert(HttpServletRequest request, JobAlertDTO jobAlertDTO) {
        JobAlert jobAlert = jobAlertRepository.findById(jobAlertDTO.getId()).get();
        jobAlert.setExperience(jobAlertDTO.getExperience());
        jobAlert.setJobType(jobAlertDTO.getJobType());
        jobAlert.setLocation(jobAlertDTO.getLocation());

        List<Skill> jobSkills=jobAlertDTO.getSkills()
                .stream()
                .map(SkillDTO::fromDTOtoEntity)
                .collect(Collectors.toList());

        // update existing skills
        if (jobSkills != null) {
            for (Skill skill : jobSkills) {
                if (skill.getId() != null) {
                    // if skill has an ID => updated
                    Skill existingSkill = skillRepository.findById(skill.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found"));
                    existingSkill.setSkill(skill.getSkill());
                    skillRepository.save(existingSkill);
                }
            }
        }

        // Add new skills
        List<Skill> newSkills = new ArrayList<>();
        if (jobSkills != null) {
            for (Skill skill : jobSkills) {
                if (skill.getId() == null) {
                    // If skill doesn't have an ID, it's a new skill to be added
                    Skill newSkill = new Skill();
                    newSkill.setSkill(skill.getSkill());
                    skillRepository.save(newSkill); // Save the new skill
                    newSkills.add(newSkill);
                }
            }
        }

        // add new skills to the existing skills list
        jobAlert.getSkills().addAll(newSkills);



        jobAlertRepository.save(jobAlert);

        return JobAlertDTO.fromEntityToDTO(jobAlert);
    }

    @Override
    public List<JobAlertDTO> getJobAlerts(HttpServletRequest request) {
        User user = userService.getUserByToken(request);
        List<JobAlert> jobAlerts= jobAlertRepository.findByUser(user);

         return jobAlerts.stream()
                .map(jobAlert -> new JobAlertDTO().fromEntityToDTO(jobAlert))
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 */3 * * * *")
    //@Scheduled(cron = "0 0 0 * * ?")
    public void sendAlert() throws MessagingException {
        System.out.println("send job alert begin");
        List<JobAlert> jobAlerts=jobAlertRepository.findAll();
        List<Job> jobs=jobRepository.findAll();
        for (JobAlert jobAlert: jobAlerts) {
            for (Job job: jobs) {
                System.out.println("-----------------comparing dates-----------------------");
                if( job.getCreated_at().isAfter(jobAlert.getCreated_at()) ){
                    List<String> jobSkills = job.getSkills().stream()
                            .map(Skill::getSkill)
                            .sorted()
                            .collect(Collectors.toList());

                    List<String> jobAlertSkills = jobAlert.getSkills().stream()
                            .map(Skill::getSkill)
                            .sorted()
                            .collect(Collectors.toList());

                    if (jobSkills.containsAll(jobAlertSkills)) {
                        System.out.println("sending email to..." + jobAlert.getUser().getEmail());
                        //jobAlert.setSent(true);
                        //sendJobAlertEmail(jobAlert.getUser().getEmail());

                        sendEmail(jobAlert.getUser().getEmail(), job);
                    }

                }
            }
        }
    }

    private void sendEmail(String email,Job job) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        try {
            //setting email parameters
            String fromEmail= env.getProperty("spring.mail.username");
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(email);
            messageHelper.setSubject("JobFinder - new matching job");

            /*
            //qr code
            // Create a URL for the QR code (replace 'job-id' with the actual job ID)
            String qrCodeUrl = "http://localhost:4200/job-detail/" + job.getId();

            // Generate the QR code image
            int width = 250;
            int height = 250;
            BitMatrix bitMatrix = new QRCodeWriter().encode(qrCodeUrl, BarcodeFormat.QR_CODE, width, height);
            // Convert the BufferedImage to a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
            */

            /*
            String qrCodeUrl = "http://localhost:4200/job-detail/" + job.getId();

            QRCodeWriter qrCodeWriter= new QRCodeWriter();
            BitMatrix bitMatrix= qrCodeWriter.encode(qrCodeUrl, BarcodeFormat.QR_CODE,200,200);
            //convert into a buffered image
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            //convert into a byte Array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);
            //convet into a base 64 encoded string
            String qrCodeBase64 = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            System.out.println("Base 64 QR code"+ qrCodeBase64);

             */

            String qrCodeUrl = "http://localhost:4200/job-detail/" + job.getId();
            //generate  qrcode
            int width = 200;
            int height = 200;
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeUrl, BarcodeFormat.QR_CODE, width, height);

            String qrCodeFileName = "qr_code_" + job.getId() + ".png";
            String qrCodeFilePath = "C:\\Users\\Mohamed\\Desktop\\Malek\\learning\\springSecurity\\src\\main\\resources\\qr_codes\\" + qrCodeFileName;

            //save the qrcode img to the file
            File qrCodeFile = new File(qrCodeFilePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrCodeFile.toPath());


            //creating context object for Thymeleaf
            Context thymeleafContext = new Context();


            thymeleafContext.setVariable("job",job);
            thymeleafContext.setVariable("qrCodeFilePath", qrCodeFilePath);

            //thymeleafContext.setVariable("qrCodeImage",qrCodeBase64 );

            //using Thymeleaf to process email template with context object
            String htmlBody = templateEngine.process("jobEmail.html", thymeleafContext);
            messageHelper.setText(htmlBody, true);


            //sending email
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email");
        }
    }


    private void sendEmail1(String email,Job job) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        try {
            //setting email parameters
            String fromEmail= env.getProperty("spring.mail.username");
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(email);
            messageHelper.setSubject("JobFinder - new matching job");

            //creating context object for Thymeleaf
            Context thymeleafContext = new Context();


            thymeleafContext.setVariable("job",job);

            //using Thymeleaf to process email template with context object
            String htmlBody = templateEngine.process("jobEmail.html", thymeleafContext);
            messageHelper.setText(htmlBody, true);


            //sending email
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email");
        }
    }

    public void sendJobAlertEmail(String email)throws MessagingException {
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage);
        //setting email parameters
        String fromEmail= env.getProperty("spring.mail.username");
        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("JobFinder - new matching job");


        mimeMessageHelper.setText("""
               <div>
                    we found a new matching job for you !
               </div>
                """.formatted(email),true);


        //send email
        javaMailSender.send(mimeMessage);

    }

}

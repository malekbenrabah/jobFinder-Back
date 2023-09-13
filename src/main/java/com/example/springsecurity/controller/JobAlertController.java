package com.example.springsecurity.controller;

import com.example.springsecurity.dto.JobAlertDTO;
import com.example.springsecurity.entity.JobAlert;
import com.example.springsecurity.service.JobAlert.IJobAlertService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobAlert")

public class JobAlertController {

    @Autowired
    IJobAlertService jobAlertService;

    @PostMapping("/createJobAlert")
    public JobAlertDTO createJobAlert(@NonNull HttpServletRequest request, @RequestBody JobAlert jobAlert) {
        return jobAlertService.createJobAlert(request, jobAlert);
    }

    @DeleteMapping("/deleteJobAlert")
    public void deleteJobAlert(@NonNull HttpServletRequest request, @RequestParam("id") Integer id) {
            jobAlertService.deleteJobAlert(request,id);
    }

    @PutMapping("/updateJobAlert")
    public JobAlertDTO updateJobAlert(@NonNull HttpServletRequest request, @RequestBody JobAlertDTO jobAlertDTO){
        return jobAlertService.updateJobAlert(request,jobAlertDTO);
    }

    @GetMapping("/getJobAlerts")
    public List<JobAlertDTO> getJobAlerts(@NonNull HttpServletRequest request) {
        return  jobAlertService.getJobAlerts(request);
    }

    @PostMapping("/sendEmail")
    public void sendAlert() throws MessagingException{

    }


}

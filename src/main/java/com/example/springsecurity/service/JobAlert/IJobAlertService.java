package com.example.springsecurity.service.JobAlert;

import com.example.springsecurity.dto.JobAlertDTO;
import com.example.springsecurity.entity.JobAlert;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IJobAlertService {

    public JobAlertDTO createJobAlert(HttpServletRequest request, JobAlert jobAlert);

    public void deleteJobAlert(HttpServletRequest request, Integer id);

    public JobAlertDTO updateJobAlert(HttpServletRequest request, JobAlertDTO jobAlertDTO);

    public List<JobAlertDTO> getJobAlerts(HttpServletRequest request);

}

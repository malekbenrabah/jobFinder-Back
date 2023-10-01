package com.example.springsecurity.service.Education;

import com.example.springsecurity.dto.EducationDTO;
import com.example.springsecurity.dto.ExperienceDTO;
import com.example.springsecurity.entity.Education;
import com.example.springsecurity.entity.Experience;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IEducationService {

    public EducationDTO addEducation(HttpServletRequest request, Education education);

    public List<EducationDTO> getUserEducation(HttpServletRequest request);
    public List<EducationDTO> getUserEducationByUserId(Integer id);
    public EducationDTO updateEducation(EducationDTO educationDTO);

    public void removeEducation(Integer id);
}

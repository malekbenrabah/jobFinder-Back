package com.example.springsecurity.service.Experience;

import com.example.springsecurity.dto.ExperienceDTO;
import com.example.springsecurity.entity.Experience;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IExperienceService {

    public ExperienceDTO addExperience(HttpServletRequest request, Experience experience);

    public List<ExperienceDTO> getUserExperiences(HttpServletRequest request);

    public ExperienceDTO updateExperience(ExperienceDTO experienceDTO);

    public void removeExperience(Integer id);
}

package com.example.springsecurity.controller;

import com.example.springsecurity.dto.ExperienceDTO;
import com.example.springsecurity.entity.Experience;
import com.example.springsecurity.service.Experience.IExperienceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/experience")
public class ExperienceController {

    @Autowired
    IExperienceService experienceService;


    @PostMapping("/addExp")
    public ExperienceDTO addExperience(@NonNull  HttpServletRequest request,
                                    @RequestBody Experience experience){

        return experienceService.addExperience(request,experience);
    }

    @GetMapping("/getExperiences")
    public List<ExperienceDTO> getUserExperiences(HttpServletRequest request) {
        return experienceService.getUserExperiences(request);
    }

    @GetMapping("/getExperienceById")
    public List<ExperienceDTO> getUserExperiencesById(@RequestParam("id") Integer id){
        return experienceService.getUserExperiencesById(id);
    }

    @PutMapping("/updateExperience")
    public ExperienceDTO updateExperience(@RequestBody  ExperienceDTO experienceDTO){
        return experienceService.updateExperience(experienceDTO);
    }

    @DeleteMapping("/removeExperience")
    public void removeExperience(@RequestParam("id") Integer id){
        experienceService.removeExperience(id);
    }


}

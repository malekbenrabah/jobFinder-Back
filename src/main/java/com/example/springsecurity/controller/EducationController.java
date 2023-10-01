package com.example.springsecurity.controller;


import com.example.springsecurity.dto.EducationDTO;
import com.example.springsecurity.dto.SkillDTO;
import com.example.springsecurity.entity.Education;
import com.example.springsecurity.entity.Skill;
import com.example.springsecurity.service.Education.IEducationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/education")
public class EducationController {

    @Autowired
    IEducationService educationService;

    @PostMapping("/addEducation")
    public EducationDTO addEducation(HttpServletRequest request,  @RequestBody Education education) {
        return educationService.addEducation(request, education);
    }

    @DeleteMapping("/deleteEducation")
    public void removeEducation(@RequestParam("id") Integer id){

        educationService.removeEducation(id);
    }

    @PutMapping("/updateEducation")
    public EducationDTO updateEducation(@RequestBody EducationDTO educationDTO) {
        return educationService.updateEducation(educationDTO);
    }

    @GetMapping("/getEducations")
    public List<EducationDTO> getUserEducation(HttpServletRequest request){
        return educationService.getUserEducation(request);
    }

    @GetMapping("/educationsByUserId")
    public List<EducationDTO> getUserEducationByUserId(@RequestParam("id") Integer id){
        return educationService.getUserEducationByUserId(id);
    }
}

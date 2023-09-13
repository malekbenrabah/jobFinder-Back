package com.example.springsecurity.controller;

import com.example.springsecurity.dto.SkillDTO;
import com.example.springsecurity.entity.Skill;
import com.example.springsecurity.service.Skills.ISkillsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
public class SkillController {

    @Autowired
    ISkillsService skillsService;

    @PostMapping("/addSkill")
    public Skill addSkill(@NonNull HttpServletRequest request, @RequestBody Skill skill) {
        return skillsService.addSkill(request, skill);
    }

    @DeleteMapping("/deleteSkill")
    public void RemoveSkill(@RequestParam("id") int id) {
        skillsService.RemoveSkill(id);
    }

    @PutMapping("/updateSkill")
    public void UpdateSkill(@RequestBody SkillDTO skillDTO) {
        skillsService.UpdateSkill(skillDTO);
    }

    @GetMapping("/getSkills")
    public List<SkillDTO> getUserSkills(@NonNull HttpServletRequest request){
        return skillsService.getUserSkills(request);
    }
}

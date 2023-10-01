package com.example.springsecurity.service.Skills;

import com.example.springsecurity.dto.SkillDTO;
import com.example.springsecurity.entity.Skill;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ISkillsService {

    public Skill addSkill(HttpServletRequest request, Skill skill);

    public void RemoveSkill(int id);

    public void UpdateSkill(SkillDTO skillDTO);

    public List<SkillDTO> getUserSkills(HttpServletRequest request);

    public List<SkillDTO> getUserSkillsById(Integer id);
}

package com.example.springsecurity.service.Skills;

import com.example.springsecurity.dto.SkillDTO;
import com.example.springsecurity.entity.Skill;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.repository.SkillRepository;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.service.IUserService;
import com.example.springsecurity.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillsService implements ISkillsService{

    @Autowired
    SkillRepository skillRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IUserService userService;


    @Override
    public Skill addSkill(HttpServletRequest request, Skill skill) {
        User user= userService.getUserByToken(request);
        user.getSkills().add(skill);
        skill.setUser(user);
        userRepository.save(user);
        skillRepository.save(skill);
        return skill;
    }

    @Override
    public void RemoveSkill(int id) {
        Skill skill =skillRepository.findById(id).get();
        skillRepository.delete(skill);
    }

    @Override
    public void UpdateSkill(SkillDTO skillDTO) {
       Skill skill = skillRepository.findById(skillDTO.getId()).get();
       skill.setSkill(skillDTO.getSkill());
       skill.setLevel(skillDTO.getLevel());
       skillRepository.save(skill);

    }

    @Override
    public List<SkillDTO> getUserSkills(HttpServletRequest request) {
        User user=userService.getUserByToken(request);
        List<Skill> skills= skillRepository.findByUser(user);

        return skills.stream()
                .map(skill -> new SkillDTO().fromEntityToDTO(skill))
                .collect(Collectors.toList());
    }
}

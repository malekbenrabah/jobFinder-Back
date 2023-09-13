package com.example.springsecurity.service.Experience;


import com.example.springsecurity.dto.ExperienceDTO;
import com.example.springsecurity.dto.SkillDTO;
import com.example.springsecurity.entity.Experience;
import com.example.springsecurity.entity.Skill;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.repository.ExperienceRepository;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienceService  implements IExperienceService{
    @Autowired
    ExperienceRepository experienceRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IUserService userService;

    @Override
    public ExperienceDTO addExperience(HttpServletRequest request, Experience experience) {
        User user = userService.getUserByToken(request);
        experience.setUser(user);
        user.getExperiences().add(experience);

        userRepository.save(user);
        experienceRepository.save(experience);

        return  ExperienceDTO.fromEntityToDTO(experience);

    }

    @Override
    public List<ExperienceDTO> getUserExperiences(HttpServletRequest request) {
        User user=userService.getUserByToken(request);
        List<Experience> experiences= experienceRepository.findByUser(user);
        return experiences.stream()
                .map(experience -> new ExperienceDTO().fromEntityToDTO(experience))
                .collect(Collectors.toList());
    }

    @Override
    public ExperienceDTO updateExperience(ExperienceDTO experienceDTO) {
        Experience experience = experienceRepository.findById(experienceDTO.getId()).get();

        experience.setDescription(experienceDTO.getDescription());
        experience.setLocation(experienceDTO.getLocation());
        experience.setStartDate(experienceDTO.getStartDate());
        experience.setEndDate(experienceDTO.getEndDate());

        experienceRepository.save(experience);

        return ExperienceDTO.fromEntityToDTO(experience);
    }

    @Override
    public void removeExperience(Integer id) {
        Experience experience =experienceRepository.findById(id).get();
        experienceRepository.delete(experience);
    }
}

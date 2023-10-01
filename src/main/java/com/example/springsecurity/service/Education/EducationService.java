package com.example.springsecurity.service.Education;

import com.example.springsecurity.dto.EducationDTO;
import com.example.springsecurity.dto.ExperienceDTO;
import com.example.springsecurity.entity.Education;
import com.example.springsecurity.entity.Experience;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.repository.EducationRepository;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EducationService implements IEducationService{

    @Autowired
    EducationRepository educationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IUserService userService;

    @Override
    public EducationDTO addEducation(HttpServletRequest request, Education education) {
        User user = userService.getUserByToken(request);
        education.setUser(user);
        user.getEducations().add(education);

        userRepository.save(user);
        educationRepository.save(education);

        return  EducationDTO.fromEntityToDTO(education);
    }

    @Override
    public List<EducationDTO> getUserEducation(HttpServletRequest request) {
        User user=userService.getUserByToken(request);
        List<Education> educations= educationRepository.findByUser(user);
        return educations.stream()
                .map(education -> new EducationDTO().fromEntityToDTO(education))
                .collect(Collectors.toList());
    }

    @Override
    public List<EducationDTO> getUserEducationByUserId(Integer id) {
        User user= userRepository.findById(id).orElse(null);
        List<Education> educations= educationRepository.findByUser(user);
        return educations.stream()
                .map(education -> new EducationDTO().fromEntityToDTO(education))
                .collect(Collectors.toList());
    }

    @Override
    public EducationDTO updateEducation(EducationDTO educationDTO) {
        Education education = educationRepository.findById(educationDTO.getId()).get();

        education.setDescription(educationDTO.getDescription());
        education.setInstitution(educationDTO.getInstitution());
        education.setStartDate(educationDTO.getStartDate());
        education.setEndDate(educationDTO.getEndDate());

        educationRepository.save(education);

        return EducationDTO.fromEntityToDTO(education);
    }

    @Override
    public void removeEducation(Integer id) {
        Education education =educationRepository.findById(id).get();
        educationRepository.delete(education);
    }
}

package com.example.springsecurity.dto;

import com.example.springsecurity.entity.Experience;
import com.example.springsecurity.entity.Skill;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceDTO {

    private Integer id;

    private String description;

    private String location;

    private LocalDate startDate;

    private LocalDate endDate;

    public static ExperienceDTO fromEntityToDTO(Experience experience){

        ExperienceDTO experienceDTO = ExperienceDTO.builder()
                .id(experience.getId())
                .description(experience.getDescription())
                .location(experience.getLocation())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .build();

        return experienceDTO;
    }

    public static Experience fromDTOtoEntity(ExperienceDTO experienceDTO){

        Experience experience=Experience.builder()
                .id(experienceDTO.getId())
                .description(experienceDTO.getDescription())
                .location(experienceDTO.getLocation())
                .startDate(experienceDTO.getStartDate())
                .endDate(experienceDTO.getEndDate())
                .build();
        return experience;

    }


}

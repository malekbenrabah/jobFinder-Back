package com.example.springsecurity.dto;


import com.example.springsecurity.entity.Education;
import com.example.springsecurity.entity.Experience;
import lombok.*;

import java.time.LocalDate;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EducationDTO {

    private Integer id;

    private String description;

    private String institution;

    private LocalDate startDate;

    private LocalDate endDate;

    public static EducationDTO fromEntityToDTO(Education education){
        EducationDTO educationDTO = EducationDTO.builder()
                .id(education.getId())
                .description(education.getDescription())
                .institution(education.getInstitution())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .build();
        return educationDTO;
    }


    public static Education fromDTOtoEntity(EducationDTO educationDTO){

        Education education=Education.builder()
                .id(educationDTO.getId())
                .description(educationDTO.getDescription())
                .institution(educationDTO.getInstitution())
                .startDate(educationDTO.getStartDate())
                .endDate(educationDTO.getEndDate())
                .build();
        return education;

    }


}

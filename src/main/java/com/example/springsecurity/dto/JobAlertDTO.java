package com.example.springsecurity.dto;

import com.example.springsecurity.entity.JobAlert;
import com.example.springsecurity.entity.JobType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobAlertDTO {

    private Integer id;

    private Integer experience;

    private String location;

    private JobType jobType;

    private LocalDateTime created_at;

    private List<SkillDTO> skills;



    public static JobAlertDTO fromEntityToDTO(JobAlert jobAlert){

        JobAlertDTO.JobAlertDTOBuilder jobAlertDTO = JobAlertDTO.builder()

            .id(jobAlert.getId())
            .experience(jobAlert.getExperience())
            .location(jobAlert.getLocation())
            .jobType(jobAlert.getJobType())
            .created_at(jobAlert.getCreated_at());

            if(jobAlert.getSkills()!=null){
                jobAlertDTO.skills(jobAlert.getSkills()
                        .stream()
                        .map(SkillDTO::fromEntityToDTO)
                        .collect(Collectors.toList()));
            }

        return jobAlertDTO.build();
    }

    public static JobAlert fromDTOtoEntity(JobAlertDTO jobAlertDTO){

        JobAlert.JobAlertBuilder jobAlert=JobAlert.builder()
                .id(jobAlertDTO.getId())
                .experience(jobAlertDTO.getExperience())
                .location(jobAlertDTO.getLocation())
                .jobType(jobAlertDTO.getJobType())
                .created_at(jobAlertDTO.getCreated_at());




        if (jobAlertDTO.getSkills() != null) {
            jobAlert.skills(jobAlertDTO.getSkills()
                    .stream()
                    .map(SkillDTO::fromDTOtoEntity)
                    .collect(Collectors.toList()));
        }

        return jobAlert.build();

    }

}

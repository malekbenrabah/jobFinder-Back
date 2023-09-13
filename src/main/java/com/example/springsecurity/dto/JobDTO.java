package com.example.springsecurity.dto;

import com.example.springsecurity.entity.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobDTO {

    private Integer id;

    private String title;

    private String description;

    private JobType jobType;

    private Integer experience;

    private LocalDateTime created_at;

    private  String location;

    private LocalDateTime deadline;

    private Sector sector;

    private String diploma;

    private List<SkillDTO> skills;

    private UserDTO companyName;

    private List<UserDTO> users;


    public static JobDTO fromEntityToDTO(Job job){

        JobDTO.JobDTOBuilder jobDTO = JobDTO.builder()

                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .jobType(job.getJobType())
                .experience(job.getExperience())
                .created_at(job.getCreated_at())
                .location(job.getLocation())
                .deadline(job.getDeadline())
                .sector(job.getSector())
                .diploma(job.getDiploma())
                .companyName(new UserDTO().fromEntityToDTO(job.getCompany()));

            if (job.getUsers() != null) {
                jobDTO.users(job.getUsers()
                        .stream()
                        .map(UserDTO::fromEntityToDTO)
                        .collect(Collectors.toList()));
            }

            if(job.getSkills()!=null){
                jobDTO.skills(job.getSkills()
                        .stream()
                        .map(SkillDTO::fromEntityToDTO)
                        .collect(Collectors.toList()));
            }

        return jobDTO.build();
    }

    public static Job fromDTOtoEntity(JobDTO jobDTO){

        Job.JobBuilder job=Job.builder()
                .id(jobDTO.getId())
                .title(jobDTO.getTitle())
                .description(jobDTO.getDescription())
                .jobType(jobDTO.getJobType())
                .experience(jobDTO.getExperience())
                .created_at(jobDTO.getCreated_at())
                .location(jobDTO.getLocation())
                .deadline(jobDTO.getDeadline())
                .sector(jobDTO.getSector())
                .diploma(jobDTO.getDiploma())
                .company(new UserDTO().fromDTOtoEntity(jobDTO.getCompanyName()));

        if (jobDTO.getUsers() != null) {
            job.users(jobDTO.getUsers()
                    .stream()
                    .map(UserDTO::fromDTOtoEntity)
                    .collect(Collectors.toList()));
        }

        if (jobDTO.getSkills() != null) {
            job.skills(jobDTO.getSkills()
                    .stream()
                    .map(SkillDTO::fromDTOtoEntity)
                    .collect(Collectors.toList()));
        }

        return job.build();

    }

}

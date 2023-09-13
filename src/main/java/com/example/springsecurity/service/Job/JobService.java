package com.example.springsecurity.service.Job;

import com.example.springsecurity.dto.ExperienceDTO;
import com.example.springsecurity.dto.JobDTO;
import com.example.springsecurity.dto.SkillDTO;
import com.example.springsecurity.entity.*;
import com.example.springsecurity.exception.ResetPasswordResponse;
import com.example.springsecurity.repository.JobRepository;
import com.example.springsecurity.repository.SkillRepository;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.service.IUserService;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class JobService  implements IJobService{

    @Autowired
    JobRepository jobRepository;

    @Autowired
    IUserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SkillRepository skillRepository;



    @Override
    public JobDTO addJob(HttpServletRequest request, Job job) {
        User user = userService.getUserByToken(request);

        if(Role.USER.equals(user.getRole())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to upload a job");
        }



        user.getCompanyJobs().add(job);
        job.setCompany(user);
        job.setCreated_at(LocalDateTime.now());

        jobRepository.save(job);

        List<Skill> skills= job.getSkills();

        for (Skill skill:skills) {
            skill.setJob(job);
            skillRepository.save(skill);
        }
        userRepository.save(user);


        return JobDTO.fromEntityToDTO(job);

    }

    @Override
    public JobDTO updateJob(HttpServletRequest request, JobDTO jobDTO) {

        User user = userService.getUserByToken(request);
        if(Role.USER.equals(user.getRole())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this job");
        }

        Job job = jobRepository.findById(jobDTO.getId()).get();

        if(user.getId()!= job.getCompany().getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this job");
        }

        job.setTitle(jobDTO.getTitle());
        job.setDescription(jobDTO.getDescription());
        job.setJobType(jobDTO.getJobType());
        job.setExperience(jobDTO.getExperience());



        List<Skill> jobSkills=jobDTO.getSkills()
                .stream()
                .map(SkillDTO::fromDTOtoEntity)
                .collect(Collectors.toList());
        // update existing skills
        if (jobSkills != null) {
            for (Skill skill : jobSkills) {
                if (skill.getId() != null) {
                    // if skill has an ID => updated
                    Skill existingSkill = skillRepository.findById(skill.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found"));
                    existingSkill.setSkill(skill.getSkill());
                    existingSkill.setJob(job);
                    skillRepository.save(existingSkill);
                }
            }
        }

        // add new skills
        List<Skill> newSkills = new ArrayList<>();
        if (jobSkills != null) {
            for (Skill skill : jobSkills) {
                if (skill.getId() == null) {
                    // if skill doesn't have an ID, it's a new skill to be added
                    Skill newSkill = new Skill();
                    newSkill.setSkill(skill.getSkill());
                    newSkill.setJob(job);
                    skillRepository.save(newSkill);
                    newSkills.add(newSkill);
                }
            }
        }

        // add new skills to the existing skills list
        job.getSkills().addAll(newSkills);

        jobRepository.save(job);
        return jobDTO.fromEntityToDTO(job);


    }

    @Override
    public void deleteJob(HttpServletRequest request, Integer id) {

        User user = userService.getUserByToken(request);
        if(Role.USER.equals(user.getRole())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this job");
        }

        Job job = jobRepository.findById(id).get();

        if(user.getId()!= job.getCompany().getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this job");
        }
        List<Skill> jobSkills= job.getSkills();
        for (Skill skill:jobSkills) {
            skillRepository.delete(skill);
        }
        jobRepository.delete(job);
    }

    @Override
    public List<JobDTO> getJobs() {
        List<Job> jobs = jobRepository.findAll();

        return jobs.stream()
                .map(job -> new JobDTO().fromEntityToDTO(job))
                .collect(Collectors.toList());

    }

    @Override
    public ApplyJobResponse applyJob(HttpServletRequest request, Integer id) {
        User user = userService.getUserByToken(request);

        if(Role.COMPANY.equals(user.getRole())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Companies can't apply for jobs");
        }

        Job job = jobRepository.findById(id).get();

        job.getUsers().add(user);
        user.getJobs().add(job);
        userRepository.save(user);
        jobRepository.save(job);

        return ApplyJobResponse.builder()
                .success(true)
                .message("applied successfully")
                .build();
    }

    @Override
    public List<JobDTO> getCompanyPostedJobs(HttpServletRequest request) {
        User user = userService.getUserByToken(request);

        List<Job> jobs = jobRepository.findByCompany(user);

        return jobs.stream()
                .map(job -> new JobDTO().fromEntityToDTO(job))
                .collect(Collectors.toList());

    }

    @Override
    public List<JobDTO> getUserAppliedJobs(HttpServletRequest request) {
        System.out.println("get user's applied jobs starting");
        User user = userService.getUserByToken(request);

        List<Job> jobs = jobRepository.findByUsers(user);

        return jobs.stream()
                .map(job -> new JobDTO().fromEntityToDTO(job))
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> fetchJobsBySkills(List<String> skills) {
        List<Job> jobs = jobRepository.fetchJobsBySkills(skills);
        return jobs.stream()
                .map(job -> new JobDTO().fromEntityToDTO(job))
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> serachJobs(String title, String description, JobType jobType, Integer experience,
                                   String location, Sector sector, String diploma,List<String> skills) {

        List<Job> jobs = jobRepository.findAll((Specification<Job>) (root, cq, cb) -> {
            Predicate p = cb.conjunction();
            if (title != null) {
                p = cb.and(p, cb.like(root.get("title"), "%" + title + "%"));
            }

            if (description != null) {
                p = cb.and(p, cb.like(root.get("description"), "%" + description + "%"));
            }

            if (jobType != null) {
                p = cb.and(p, cb.equal(root.get("jobType"), jobType));
            }
            if (experience != null) {
                p = cb.and(p, cb.equal(root.get("experience"), experience));
            }
            if (location!= null) {
                p = cb.and(p, cb.equal(root.get("location"),location ));
            }
            if (sector != null) {
                p = cb.and(p, cb.equal(root.get("sector"), sector));
            }
            if (diploma != null) {
                p = cb.and(p, cb.equal(root.get("diploma"), diploma));
            }
            if (skills != null && !skills.isEmpty()) {
                Join<Job, Skill> skillJoin = root.join("skills", JoinType.INNER);
                Expression<String> skillExpression = skillJoin.get("skill");
                Predicate skillPredicate = skillExpression.in(skills);
                p = cb.and(p, skillPredicate);
            }

            cq.orderBy(cb.desc(root.get("created_at")));
            return p;
        });


        return jobs.stream()
                .map(job -> new JobDTO().fromEntityToDTO(job))
                .collect(Collectors.toList());


    }


}

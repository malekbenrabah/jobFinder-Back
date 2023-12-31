package com.example.springsecurity.service.Job;

import com.example.springsecurity.dto.ExperienceDTO;
import com.example.springsecurity.dto.JobByMonthDTO;
import com.example.springsecurity.dto.JobDTO;
import com.example.springsecurity.dto.SkillDTO;
import com.example.springsecurity.entity.*;
import com.example.springsecurity.exception.InvalidOldPasswordException;
import com.example.springsecurity.exception.ResetPasswordResponse;
import com.example.springsecurity.repository.JobRepository;
import com.example.springsecurity.repository.SkillRepository;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.service.IUserService;
import com.example.springsecurity.service.JobAlert.UserAlreadyAppliedException;
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


        for (User u:job.getUsers()) {
            u.getJobs().remove(job);
        }
        job.getUsers().clear();


        jobRepository.delete(job);
    }

    @Override
    public void deleteJobByAdmin(Integer id) {
        Job job = jobRepository.findById(id).get();
        List<Skill> jobSkills= job.getSkills();
        for (Skill skill:jobSkills) {
            skillRepository.delete(skill);
        }
        for (User u:job.getUsers()) {
            u.getJobs().remove(job);
        }
        job.getUsers().clear();

        jobRepository.delete(job);
    }

    @Override
    public void deleteJobSkill(Integer id, Integer skillId) {
        Job job = jobRepository.findById(id).get();


        List<Skill> jobSkills= job.getSkills();
        for (Skill skill:jobSkills) {

            if(skill.getId().equals(skillId)){
                skillRepository.delete(skill);
            }

        }
    }

    @Override
    public List<JobDTO> getJobs() {
        List<Job> jobs = jobRepository.findJobsDesc();

        return jobs.stream()
                .map(job -> new JobDTO().fromEntityToDTO(job))
                .collect(Collectors.toList());

    }

    @Override
    public Integer nbJobs() {
        return jobRepository.findAll().size();
    }

    @Override
    public ApplyJobResponse applyJob(HttpServletRequest request, Integer id) {
        User user = userService.getUserByToken(request);

        if(Role.COMPANY.equals(user.getRole())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Companies can't apply for jobs");
        }

        Job job = jobRepository.findById(id).get();

        if(job.getUsers().contains(user)){
            throw new UserAlreadyAppliedException("You have already applied to this job");

        }

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

        List<Job> jobs = jobRepository.fetchCompanyJobs(user);

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
    public List<JobDTO> searchJobs(String title, String description, JobType jobType, String experience,
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
                if (experience.equals("Over 6 years")) {
                    p = cb.and(p, cb.greaterThan(root.get("experience"), 6));
                } else if (experience.equals("No experience")) {
                    p = cb.and(p, cb.equal(root.get("experience"), 0));
                } else {
                    // parsing the experience range into minimum and maximum values
                    String[] rangeParts = experience.split("-");
                    if (rangeParts.length == 2) {
                        int minExperience = Integer.parseInt(rangeParts[0].trim());
                        int maxExperience = Integer.parseInt(rangeParts[1].trim());
                        p = cb.and(p, cb.between(root.get("experience"), minExperience, maxExperience));
                    }
                }
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

    @Override
    public JobDTO getJobById(Integer id) {
        Job job = jobRepository.findById(id).get();
        return JobDTO.fromEntityToDTO(job);
    }

    @Override
    public List<JobDTO> similarJobs(Integer id) {
        List<Job> jobs=jobRepository.findAll();
        Job job = jobRepository.findById(id).get();
        List<Job> similarJobs= new ArrayList<>();
        for (Job j:jobs) {
            List<String> jobSkills = job.getSkills().stream()
                    .map(Skill::getSkill)
                    .sorted()
                    .collect(Collectors.toList());

            List<String> comparedSkills = j.getSkills().stream()
                    .map(Skill::getSkill)
                    .sorted()
                    .collect(Collectors.toList());


            if (jobSkills.containsAll(comparedSkills) && job.getJobType().equals(j.getJobType()) && job.getSector().equals(j.getSector()) && job.getId()!=j.getId()) {
                similarJobs.add(j);
            }
        }
        return similarJobs.stream()
                .map(similarJob -> new JobDTO().fromEntityToDTO(similarJob))
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> companyJobs(String email){

        User user= userRepository.findUserByEmail(email);
        
        List<Job> jobs= jobRepository.findAll();
        List<Job> companyJobs=new ArrayList<>();
        for (Job j:jobs) {
            if(j.getCompany().equals(user)){
                companyJobs.add(j);
            }
        }
        return companyJobs.stream()
                .map(companyJob -> new JobDTO().fromEntityToDTO(companyJob))
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> companyOpenJobs(String email) {
        List<JobDTO> companyJobs=companyJobs(email);
        List<JobDTO>openJobs=new ArrayList<>();
        for (JobDTO jobDTO:companyJobs) {
            if(jobDTO.getDeadline().isAfter(LocalDateTime.now())){
                openJobs.add(jobDTO);
            }
        }
        return openJobs;
    }

    @Override
    public List<Object[]> getJobsByMonth() {
        return  jobRepository.getJobsByMonth();
    }

    @Override
    public List<Object[]> getJobsByJobType() {
        return jobRepository.getJobsByJobType();
    }

    @Override
    public List<Object[]> getTopCompanies() {
        return jobRepository.getTopCompanies();
    }

    @Override
    public List<JobDTO> findJobsBySector(Sector sector) {
        List<Job> jobs = jobRepository.findBySector(sector);
        return jobs.stream()
                .map(job -> new JobDTO().fromEntityToDTO(job))
                .collect(Collectors.toList());
    }


}

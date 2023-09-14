package com.example.springsecurity.controller;

import com.example.springsecurity.dto.JobDTO;
import com.example.springsecurity.entity.Job;
import com.example.springsecurity.entity.JobType;
import com.example.springsecurity.entity.Sector;
import com.example.springsecurity.entity.Skill;
import com.example.springsecurity.service.Job.ApplyJobResponse;
import com.example.springsecurity.service.Job.IJobService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    IJobService jobService;

    @PostMapping("/addJob")
    public JobDTO addJob(@NonNull HttpServletRequest request, @RequestBody Job job) {
        return jobService.addJob(request, job);
    }

    @PutMapping("/updateJob")
    public JobDTO updateJob(@NonNull HttpServletRequest request, @RequestBody JobDTO jobDTO) {
        return jobService.updateJob(request, jobDTO);
    }

    @DeleteMapping("/deleteJob")
    public void deleteJob(@NonNull HttpServletRequest request, @RequestParam("id") Integer id) {
        jobService.deleteJob(request, id);
    }


    @GetMapping("/getJobs")
    public List<JobDTO> getJobs() {
        return jobService.getJobs();
    }

    @PostMapping("/applyJob")
    public ResponseEntity<ApplyJobResponse> applyJob(@NonNull HttpServletRequest request, @RequestParam("id") Integer id) {
        return ResponseEntity.ok(this.jobService.applyJob(request, id));

    }


    @GetMapping("/getCompanyPostedJobs")
    public List<JobDTO> getCompanyPostedJobs(@NonNull HttpServletRequest request) {
        return jobService.getCompanyPostedJobs(request);
    }

    @GetMapping("/getUserAppliedJobs")
    public List<JobDTO> getUserAppliedJobs(HttpServletRequest request) {
        return jobService.getUserAppliedJobs(request);
    }

    @GetMapping("/fetchJobsBySkills")
    public List<JobDTO> fetchJobsBySkills(@RequestParam("skills") List<String> skills){
        return  jobService.fetchJobsBySkills(skills);
    }

    @GetMapping("/serachJobs")
    public List<JobDTO> serachJobs(@RequestParam(value="title",required = false)String title,
                                   @RequestParam(value="description",required = false)String description,
                                   @RequestParam(value="jobType",required = false)JobType jobType,
                                   @RequestParam(value="experience",required = false)Integer experience,
                                   @RequestParam(value="location",required = false)String location,
                                   @RequestParam(value="sector",required = false)Sector sector,
                                   @RequestParam(value="diploma",required = false)String diploma,
                                   @RequestParam(value = "skills", required = false) List<String> skills){
        return jobService.serachJobs(title,description,jobType,experience,location,sector,diploma,skills);
    }

    @GetMapping("/findJobById")
    public JobDTO getJobById(@RequestParam("id") Integer id){
        return jobService.getJobById(id);
    }









}

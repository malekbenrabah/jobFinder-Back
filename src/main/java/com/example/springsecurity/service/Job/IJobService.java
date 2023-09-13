package com.example.springsecurity.service.Job;

import com.example.springsecurity.dto.JobDTO;
import com.example.springsecurity.entity.Experience;
import com.example.springsecurity.entity.Job;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IJobService {

    public JobDTO addJob(HttpServletRequest request, Job job);

    public JobDTO updateJob(HttpServletRequest request,JobDTO jobDTO);

    public void deleteJob(HttpServletRequest request, Integer id);

    public List<JobDTO> getJobs();

    public ApplyJobResponse applyJob(HttpServletRequest request, Integer id);

    public List<JobDTO> getCompanyPostedJobs(HttpServletRequest request);

    public List<JobDTO> getUserAppliedJobs(HttpServletRequest request);


}

package com.example.springsecurity.repository;

import com.example.springsecurity.dto.JobByMonthDTO;
import com.example.springsecurity.entity.Job;
import com.example.springsecurity.entity.Skill;
import com.example.springsecurity.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {


    @Query("select j from Job j order by j.created_at desc ")
    public List<Job> findJobsDesc();

    public List<Job> findByCompany(User user);

    @Query("SELECT j FROM Job  j where j.company=:user " +
            "ORDER BY  j.created_at desc ")
    public List<Job> fetchCompanyJobs(@Param("user") User user);


    public List<Job> findByUsers(User user);

    @Query("SELECT DISTINCT j FROM Job j " +
            "INNER JOIN j.skills s " +
            "WHERE s.skill IN :skills")
    List<Job> fetchJobsBySkills(@Param("skills") List<String> skills);

    List<Job> findAll(Specification<Job> spec);

    @Query(value = "SELECT EXTRACT(MONTH FROM created_at) AS month, COUNT(*) AS jobCount FROM job GROUP BY month", nativeQuery = true)
    public List<JobByMonthDTO> getJobsByMonthss();

    @Query("select MONTH(j.created_at) as month, count(j.id) from Job j group by month")
    public List<Object[]> getJobsByMonth();

    @Query("select j.jobType, count(j.id) from Job j group by j.jobType")
    public List<Object[]> getJobsByJobType();


    @Query("select count(j.id), j.company from Job  j group by  j.company")
    public List<Object[]> getTopCompanies();




}

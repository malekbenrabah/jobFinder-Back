package com.example.springsecurity.repository;

import com.example.springsecurity.entity.Job;
import com.example.springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {

    public List<Job> findByCompany(User user);

    public List<Job> findByUsers(User user);
}

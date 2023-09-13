package com.example.springsecurity.repository;

import com.example.springsecurity.entity.JobAlert;
import com.example.springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobAlertRepository extends JpaRepository<JobAlert, Integer> {

    public List<JobAlert> findByUser(User user);
}

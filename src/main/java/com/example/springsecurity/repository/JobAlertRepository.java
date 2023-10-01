package com.example.springsecurity.repository;

import com.example.springsecurity.entity.JobAlert;
import com.example.springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobAlertRepository extends JpaRepository<JobAlert, Integer> {

    @Query("SELECT j FROM JobAlert  j where j.user=:user " +
            "ORDER BY  j.created_at desc ")
    public List<JobAlert> findByUser(@Param("user") User user);
}

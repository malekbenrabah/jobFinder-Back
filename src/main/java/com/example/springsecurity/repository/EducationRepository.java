package com.example.springsecurity.repository;

import com.example.springsecurity.entity.Education;
import com.example.springsecurity.entity.Experience;
import com.example.springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education,Integer> {

    public List<Education> findByUser(User user);

}

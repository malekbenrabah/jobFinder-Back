package com.example.springsecurity.repository;

import com.example.springsecurity.entity.Experience;
import com.example.springsecurity.entity.Skill;
import com.example.springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience,Integer> {
    public List<Experience> findByUser(User user);

}

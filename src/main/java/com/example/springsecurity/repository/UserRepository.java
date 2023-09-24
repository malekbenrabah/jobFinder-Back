package com.example.springsecurity.repository;


import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.Skill;
import com.example.springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    public Optional<User> findByEmail(String email);

    public Boolean existsByEmail(String email);

    public User findUserByEmail(String email);

    public Boolean existsByCompanyName(String companyName);


    public List<User> findByCompanyNameNotNull();

    @Query("select count (u) from User u " +
            "where u.role='USER' ")
    public Integer getNbUsers();

}

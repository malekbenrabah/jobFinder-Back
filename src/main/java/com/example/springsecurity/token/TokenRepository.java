package com.example.springsecurity.token;

import com.example.springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("select t from Token t inner join User u on t.user.id = u.id " +
            "where u.id=:userId")
    public List<Token> findAllValidTokensByUser(Integer userId);


    Optional<Token> findByToken(String token);



}

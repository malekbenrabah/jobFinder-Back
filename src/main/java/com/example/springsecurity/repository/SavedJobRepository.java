package com.example.springsecurity.repository;

import com.example.springsecurity.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedJobRepository  extends JpaRepository<SavedJob,Integer> {
}

package com.example.springsecurity.service.SavedJobs;

import com.example.springsecurity.repository.SavedJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SavedJobsService implements ISavedJobsService{
    @Autowired
    SavedJobRepository savedJobRepository;
}

package com.example.springsecurity.controller;

import com.example.springsecurity.service.SavedJobs.ISavedJobsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/savedJobs")
public class SavedJobService {

    @Autowired
    ISavedJobsService savedJobsService;


}

package com.example.springsecurity.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/management")
public class ManagementController {

    @GetMapping
    public String get(){
        return "GET:: management controller";
    }

    @PostMapping
    public String post(){
        return "POST:: management controller";
    }

    @PutMapping
    public String put(){
        return "PUT:: management controller";
    }

    @DeleteMapping
    public String delete(){
        return "Delete:: management controller";
    }
}

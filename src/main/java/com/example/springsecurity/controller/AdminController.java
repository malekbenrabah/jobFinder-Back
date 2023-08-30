package com.example.springsecurity.controller;

import com.example.springsecurity.dto.UserDTO;
import com.example.springsecurity.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @GetMapping
    public String get(){
        return "GET:: admin controller";
    }

    @PostMapping
    public String post(){
        return "POST:: admin controller";
    }

    @PutMapping
    public String put(){
        return "PUT:: admin controller";
    }

    @DeleteMapping
    public String delete(){
        return "Delete:: admin controller";
    }



}

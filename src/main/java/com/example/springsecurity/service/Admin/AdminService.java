package com.example.springsecurity.service.Admin;

import com.example.springsecurity.dto.UserDTO;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService implements IAdminService{

    @Autowired
    UserRepository userRepository;

}

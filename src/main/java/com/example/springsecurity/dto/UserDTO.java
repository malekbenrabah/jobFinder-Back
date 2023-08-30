package com.example.springsecurity.dto;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private Role role;
    private String photo;
    private LocalDateTime created_at;
    private Integer phone;

    public static UserDTO fromEntityToDTO(User user){

        UserDTO userDTO=UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .role(user.getRole())
                .photo(user.getPhoto())
                .created_at(user.getCreated_at())
                .phone(user.getPhone())
                .build();

        return userDTO;
    }

    public static User fromDTOtoEntity(UserDTO userDTO){

        User user=User.builder()
                .id(userDTO.getId())
                .firstname(userDTO.getFirstname())
                .lastname(userDTO.getLastname())
                .email(userDTO.getEmail())
                .role(userDTO.getRole())
                .photo(userDTO.getPhoto())
                .created_at(userDTO.getCreated_at())
                .phone(userDTO.getPhone())
                .build();
        return user;

    }

}

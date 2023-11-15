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
    private String companyName;
    private String adresse;
    private String aboutMe;
    private Boolean cv_created;


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
                .companyName(user.getCompanyName())
                .adresse(user.getAdresse())
                .aboutMe(user.getAboutMe())
                .cv_created(user.getCv_created())
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
                .companyName(userDTO.getCompanyName())
                .adresse(userDTO.getAdresse())
                .cv_created(userDTO.getCv_created())
                .build();
        return user;

    }

}

package com.example.springsecurity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer experience;

    private String location;

    private Boolean sent;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    private LocalDateTime created_at;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Skill> skills;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

}

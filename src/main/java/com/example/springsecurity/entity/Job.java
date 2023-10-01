package com.example.springsecurity.entity;

import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String description;

    @NonNull
    @Enumerated(EnumType.STRING)
    private JobType jobType;

    private Integer experience;

    private LocalDateTime created_at;

    private  String location;

    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private Sector sector;

    private String diploma;


    @ManyToMany(mappedBy = "jobs", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> users;

    @ManyToOne
    private User company;


    @OneToMany(mappedBy = "job",fetch = FetchType.EAGER)
    private List<Skill> skills;



}

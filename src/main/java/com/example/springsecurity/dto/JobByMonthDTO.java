package com.example.springsecurity.dto;

import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobByMonthDTO {
    private String month;
    private Integer jobCount;


}

package com.example.springsecurity.dto;

import com.example.springsecurity.entity.Skill;
import com.example.springsecurity.entity.User;
import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillDTO {

    private Integer id;

    private String skill;

    private String level;

    public static SkillDTO fromEntityToDTO(Skill skill){

        SkillDTO skillDTO = SkillDTO.builder()
                .id(skill.getId())
                .skill(skill.getSkill())
                .level(skill.getLevel())
                .build();

        return skillDTO;
    }

    public static Skill fromDTOtoEntity(SkillDTO skillDTO){

        Skill skill=Skill.builder()
                .id(skillDTO.getId())
                .skill(skillDTO.getSkill())
                .level(skillDTO.getLevel())
                .build();
        return skill;

    }
}

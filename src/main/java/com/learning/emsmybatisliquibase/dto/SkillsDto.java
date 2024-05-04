package com.learning.emsmybatisliquibase.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillsDto {
    private UUID employeeUuid;

    private String name;

    @Size(min = 1, max = 10, message = "Rating must between 1 and 10")
    private int rating;
}

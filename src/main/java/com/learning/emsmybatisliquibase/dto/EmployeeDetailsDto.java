package com.learning.emsmybatisliquibase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailsDto {

    private UUID uuid;

    private String firstName;

    private String lastName;

    private String email;
}

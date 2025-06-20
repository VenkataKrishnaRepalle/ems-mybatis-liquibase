package com.learning.emsmybatisliquibase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.learning.emsmybatisliquibase.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeAndManagerDto {
    private UUID uuid;

    private String firstName;

    private String lastName;

    private Gender gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    private Integer age;

    private String phoneNumber;

    private String email;

    private UUID managerUuid;

    private EmployeeResponseDto manager;

    private Boolean isManager;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date joiningDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date leavingDate;
}

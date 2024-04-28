package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.Gender;
import com.learning.emsmybatisliquibase.entity.ProfileStatus;
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
public class EmployeeResponseDto {
    private UUID uuid;

    private String firstName;

    private String lastName;

    private Gender gender;

    private Date dateOfBirth;

    private Integer age;

    private String phoneNumber;

    private String email;

    private UUID managerUuid;

    private Boolean isManager;

    private Date joiningDate;

    private Date leavingDate;

    private ProfileStatus status;
}

package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.enums.ProfileStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class EmployeeDepartmentAndManagerDetailsDto {

    private UUID colleagueUuid;

    private String colleagueFullName;

    private String email;

    private Date joiningDate;

    private Date leavingDate;

    @Enumerated(EnumType.STRING)
    private ProfileStatus profileStatus;

    private String managerFullName;

    private String departmentName;
}

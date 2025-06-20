package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.Department;
import com.learning.emsmybatisliquibase.entity.enums.Gender;
import com.learning.emsmybatisliquibase.entity.Profile;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddEmployeeResponseDto {

    private UUID uuid;

    private String firstName;

    private String lastName;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String phoneNumber;

    private String email;

    @Temporal(TemporalType.DATE)
    private LocalDate joiningDate;

    private Profile profile;

    private Department department;

    private Boolean isManager;
}

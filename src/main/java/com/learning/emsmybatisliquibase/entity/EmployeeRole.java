package com.learning.emsmybatisliquibase.entity;

import com.learning.emsmybatisliquibase.entity.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeRole {

    private UUID employeeUuid;

    private RoleType role;
}

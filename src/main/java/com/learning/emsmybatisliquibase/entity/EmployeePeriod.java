package com.learning.emsmybatisliquibase.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeePeriod implements Serializable {

    private UUID uuid;

    private UUID employeeUuid;

    private UUID cycleUuid;

    private PeriodStatus status;

    private Instant createdTime;

    private Instant updatedTime;
}

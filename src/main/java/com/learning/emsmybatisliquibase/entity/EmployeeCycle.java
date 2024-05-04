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
public class EmployeeCycle implements Serializable {

    private UUID uuid;

    private UUID employeeUuid;

    private UUID cycleId;

    private CycleStatus status;

    private Instant createdOn;

    private Instant lastUpdatedOn;
}

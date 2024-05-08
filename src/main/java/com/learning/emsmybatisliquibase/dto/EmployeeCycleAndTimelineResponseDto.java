package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.Cycle;
import com.learning.emsmybatisliquibase.entity.CycleStatus;
import com.learning.emsmybatisliquibase.entity.Timeline;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeCycleAndTimelineResponseDto {
    private UUID employeeId;

    private UUID employeeCycleId;

    private CycleStatus employeeCycleStatus;

    private Cycle cycle;

    private Timeline timeline;
}

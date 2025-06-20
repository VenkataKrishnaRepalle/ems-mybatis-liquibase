package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.Period;
import com.learning.emsmybatisliquibase.entity.ReviewTimeline;
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

    private Period period;

    private ReviewTimeline Q1;

    private ReviewTimeline Q2;

    private ReviewTimeline Q3;

    private ReviewTimeline Q4;
}

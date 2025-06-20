package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.enums.PeriodStatus;
import com.learning.emsmybatisliquibase.entity.ReviewTimeline;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullEmployeePeriodDto {

    private UUID uuid;

    private UUID employeeUuid;

    private UUID periodUuid;

    private PeriodStatus status;

    private List<ReviewTimeline> reviewTimelines;

    private Instant createdTime;

    private Instant updatedTime;
}

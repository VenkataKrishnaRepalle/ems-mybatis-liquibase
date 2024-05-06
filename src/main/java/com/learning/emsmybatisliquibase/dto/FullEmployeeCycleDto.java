package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.CycleStatus;
import com.learning.emsmybatisliquibase.entity.Timeline;
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
public class FullEmployeeCycleDto {

    private UUID uuid;

    private UUID employeeUuid;

    private UUID cycleUuid;

    private CycleStatus status;

    private List<Timeline> timelines;

    private Instant createdTime;

    private Instant updatedTime;
}

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
public class Timeline implements Serializable {

    private UUID uuid;

    private UUID employeeCycleUuid;

    private ReviewType reviewType;

    private Instant startTime;

    private Instant overdueTime;

    private Instant lockTime;

    private Instant endTime;

    private TimelineStatus status;

    private Instant createdTime;

    private Instant lastUpdatedTime;
}

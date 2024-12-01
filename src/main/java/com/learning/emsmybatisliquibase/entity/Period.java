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
public class Period implements Serializable {

    private UUID uuid;

    private String name;

    private String description;

    private Instant startTime;

    private Instant endTime;

    private PeriodStatus status;

    private UUID createdBy;

    private Instant createdTime;

    private Instant updatedTime;
}

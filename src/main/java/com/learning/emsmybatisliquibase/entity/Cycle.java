package com.learning.emsmybatisliquibase.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cycle implements Serializable {

    private UUID uuid;

    private String name;

    private String description;

    private Instant startTime;

    private Instant endTime;

    private CycleStatus status;

    private UUID createdBy;

    private Instant createdOn;

    private Instant lastUpdatedOn;
}

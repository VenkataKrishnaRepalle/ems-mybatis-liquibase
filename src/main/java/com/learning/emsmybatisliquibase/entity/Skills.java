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
public class Skills implements Serializable {

    private UUID uuid;

    private UUID employeeUuid;

    private String name;

    private Integer rating;

    private Instant createdTime;

    private Instant updatedTime;
}

package com.learning.emsmybatisliquibase.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Education implements Serializable {

    private UUID uuid;

    private UUID employeeUuid;

    private EducationDegree degree;

    private String schoolName;

    private Double grade;

    private Date startDate;

    private Date endDate;

    private Instant createdTime;

    private Instant updatedTime;
}

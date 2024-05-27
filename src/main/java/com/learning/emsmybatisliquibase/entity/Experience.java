package com.learning.emsmybatisliquibase.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Experience implements Serializable {

    private UUID uuid;

    private UUID employeeUuid;

    private String companyName;

    private String location;

    private String description;

    private Boolean isCurrentJob;

    private Date startDate;

    private Date endDate;
}

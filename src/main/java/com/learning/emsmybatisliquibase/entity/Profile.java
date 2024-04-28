package com.learning.emsmybatisliquibase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Profile implements Serializable {

    private JobTitleType jobTitle;

    private ProfileStatus profileStatus;

    private UUID employeeUuid;

    private UUID departmentUuid;

    @JsonIgnore
    private Instant updatedTime;
}

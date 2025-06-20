package com.learning.emsmybatisliquibase.entity;

import com.learning.emsmybatisliquibase.entity.enums.PasswordStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Password {

    private UUID uuid;

    private UUID employeeUuid;

    private String password;

    private int noOfIncorrectEntries;

    private PasswordStatus status;

    private Instant createdTime;

    private Instant updatedTime;
}

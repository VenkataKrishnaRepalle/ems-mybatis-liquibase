package com.learning.emsmybatisliquibase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    private UUID uuid;

    private String firstName;

    private String lastName;

    private String email;

    private UUID timelineUuid;

    private Instant startTime;
}

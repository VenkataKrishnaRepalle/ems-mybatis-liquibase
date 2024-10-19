package com.learning.emsmybatisliquibase.entity;

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
public class Feedback {

    private UUID uuid;

    private UUID employeeUuid;

    private UUID targetEmployeeUuid;

    private FeedbackType type;

    private String lookBack;

    private String lookForward;

    private String otherComments;

    private Instant createdTime;

    private Instant updatedTime;
}

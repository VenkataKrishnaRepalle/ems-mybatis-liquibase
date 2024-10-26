package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponseDto {

    private UUID uuid;

    private EmployeeDetailsDto sender;

    private EmployeeDetailsDto receiver;

    private FeedbackType type;

    private String lookBack;

    private String lookForward;

    private String otherComments;

    private Instant createdTime;

    private Instant updatedTime;
}

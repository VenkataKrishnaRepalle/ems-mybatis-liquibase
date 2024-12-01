package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.FeedbackType;
import lombok.Data;

import java.util.UUID;

@Data
public class RequestFeedbackDto {

    private UUID employeeUuid;

    private UUID targetEmployeeUuid;

    private FeedbackType type;
}

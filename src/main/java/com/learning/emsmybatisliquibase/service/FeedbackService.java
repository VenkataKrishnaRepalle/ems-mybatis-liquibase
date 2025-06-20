package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.FeedbackResponseDto;
import com.learning.emsmybatisliquibase.dto.RequestFeedbackDto;
import com.learning.emsmybatisliquibase.entity.Feedback;
import com.learning.emsmybatisliquibase.entity.enums.FeedbackType;

import java.util.List;
import java.util.UUID;

public interface FeedbackService {
    Feedback getById(UUID uuid);

    List<FeedbackResponseDto> getFeedback(UUID employeeUuid, FeedbackType feedbackType);

    Feedback send(Feedback feedback);

    Feedback update(UUID feedbackId, Feedback feedback);

    void delete(UUID feedbackId);

    Feedback requestFeedback(RequestFeedbackDto requestFeedbackDto);

    List<FeedbackResponseDto> getAll(UUID employeeUuid);
}

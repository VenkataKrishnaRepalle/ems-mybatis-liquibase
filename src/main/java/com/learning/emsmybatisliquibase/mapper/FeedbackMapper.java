package com.learning.emsmybatisliquibase.mapper;

import com.learning.emsmybatisliquibase.dto.FeedbackResponseDto;
import com.learning.emsmybatisliquibase.dto.RequestFeedbackDto;
import com.learning.emsmybatisliquibase.entity.Feedback;
import org.mapstruct.Mapper;

@Mapper
public interface FeedbackMapper {
    FeedbackResponseDto feedbackToFeedbackResponseDto(Feedback feedback);

    Feedback requestFeedbackDtoToFeedback(RequestFeedbackDto requestFeedbackDto);
}

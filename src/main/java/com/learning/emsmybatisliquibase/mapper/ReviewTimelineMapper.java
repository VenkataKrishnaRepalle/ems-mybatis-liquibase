package com.learning.emsmybatisliquibase.mapper;

import com.learning.emsmybatisliquibase.dto.TimelineAndReviewResponseDto;
import com.learning.emsmybatisliquibase.entity.ReviewTimeline;
import org.mapstruct.Mapper;

@Mapper
public interface ReviewTimelineMapper {
    TimelineAndReviewResponseDto timelineToTimelineAndReviewResponseDto(ReviewTimeline timeline);
}

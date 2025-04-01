package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.Review;
import com.learning.emsmybatisliquibase.entity.ReviewStatus;
import com.learning.emsmybatisliquibase.entity.ReviewTimelineStatus;
import com.learning.emsmybatisliquibase.entity.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimelineAndReviewResponseDto {
    private UUID uuid;

    private UUID employeePeriodUuid;

    private ReviewType type;

    private Instant startTime;

    private Instant overdueTime;

    private Instant lockTime;

    private Instant endTime;

    private ReviewTimelineStatus status;

    private ReviewStatus summaryStatus;

    private Review review;

    private Instant createdTime;

    private Instant updatedTime;
}

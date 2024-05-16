package com.learning.emsmybatisliquibase.service;


import com.learning.emsmybatisliquibase.dto.AddReviewRequestDto;
import com.learning.emsmybatisliquibase.entity.Review;

import java.util.UUID;

public interface ReviewService {
    Review add(UUID employeeUuid, AddReviewRequestDto employeeReviewDto);

    Review update(UUID employeeUuid, UUID reviewUuid, Review review);

    Review getById(UUID reviewUuid);

    void deleteById(UUID reviewUuid);
}

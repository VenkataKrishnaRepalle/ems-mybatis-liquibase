package com.learning.emsmybatisliquibase.service;


import com.learning.emsmybatisliquibase.dto.AddEmployeeReviewRequestDto;
import com.learning.emsmybatisliquibase.dto.AddEmployeeReviewResponseDto;

public interface ReviewService {
    AddEmployeeReviewResponseDto add(AddEmployeeReviewRequestDto employeeReviewDto);
}

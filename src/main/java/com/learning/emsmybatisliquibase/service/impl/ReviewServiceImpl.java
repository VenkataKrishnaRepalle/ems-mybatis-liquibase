package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.ReviewDao;
import com.learning.emsmybatisliquibase.dto.AddEmployeeReviewRequestDto;
import com.learning.emsmybatisliquibase.dto.AddEmployeeReviewResponseDto;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import com.learning.emsmybatisliquibase.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao;

    private final EmployeeService employeeService;

    @Override
    public AddEmployeeReviewResponseDto add(AddEmployeeReviewRequestDto employeeReviewDto) {
        return null;
    }
}

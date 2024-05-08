package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.AddEmployeeReviewRequestDto;
import com.learning.emsmybatisliquibase.dto.AddEmployeeReviewResponseDto;
import com.learning.emsmybatisliquibase.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<AddEmployeeReviewResponseDto> add(@RequestBody AddEmployeeReviewRequestDto employeeReviewDto) {
        return new ResponseEntity<>(reviewService.add(employeeReviewDto), HttpStatus.CREATED);
    }
}

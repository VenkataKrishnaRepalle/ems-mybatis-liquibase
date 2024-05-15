package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.AddReviewRequestDto;
import com.learning.emsmybatisliquibase.entity.Review;
import com.learning.emsmybatisliquibase.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/add/{employeeUuid}")
    public ResponseEntity<Review> add(@PathVariable UUID employeeUuid, @RequestBody AddReviewRequestDto employeeReviewDto) {
        return new ResponseEntity<>(reviewService.add(employeeUuid, employeeReviewDto), HttpStatus.CREATED);
    }

    @PutMapping("/update/{employeeUuid}/review/{reviewUuid}")
    public ResponseEntity<Review> update(@PathVariable UUID reviewUuid, @RequestBody Review review, @PathVariable UUID employeeUuid) {
        return new ResponseEntity<>(reviewService.update(employeeUuid, reviewUuid, review), HttpStatus.ACCEPTED);
    }

    @GetMapping("/getById/{reviewUuid}")
    public ResponseEntity<Review> getById(@PathVariable UUID reviewUuid) {
        return new ResponseEntity<>(reviewService.getById(reviewUuid), HttpStatus.OK);
    }
}

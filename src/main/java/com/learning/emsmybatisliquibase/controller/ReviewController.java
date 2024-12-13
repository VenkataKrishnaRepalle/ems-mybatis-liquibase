package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.AddReviewRequestDto;
import com.learning.emsmybatisliquibase.entity.Review;
import com.learning.emsmybatisliquibase.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;


import java.util.UUID;

@RestController
@RequestMapping("api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/add/{employeeUuid}")
    public ResponseEntity<Review> add(@PathVariable UUID employeeUuid,
                                      @RequestBody AddReviewRequestDto employeeReviewDto) {
        return new ResponseEntity<>(reviewService.add(employeeUuid, employeeReviewDto),
                HttpStatus.CREATED);
    }

    @PutMapping("/update/{employeeUuid}/review/{reviewUuid}")
    public ResponseEntity<Review> update(@PathVariable UUID reviewUuid, @RequestBody Review review,
                                         @PathVariable UUID employeeUuid) {
        return new ResponseEntity<>(reviewService.update(employeeUuid, reviewUuid, review),
                HttpStatus.ACCEPTED);
    }

    @GetMapping("/getById/{reviewUuid}")
    public ResponseEntity<Review> getById(@PathVariable UUID reviewUuid) {
        return new ResponseEntity<>(reviewService.getById(reviewUuid), HttpStatus.OK);
    }

    @DeleteMapping("/deleteById/{reviewUuid}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable UUID reviewUuid) {
        reviewService.deleteById(reviewUuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

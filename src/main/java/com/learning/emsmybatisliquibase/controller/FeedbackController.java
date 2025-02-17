package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.FeedbackResponseDto;
import com.learning.emsmybatisliquibase.dto.RequestFeedbackDto;
import com.learning.emsmybatisliquibase.entity.Feedback;
import com.learning.emsmybatisliquibase.entity.FeedbackType;
import com.learning.emsmybatisliquibase.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping("/getById/{uuid}")
    public ResponseEntity<Feedback> getById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(feedbackService.getById(uuid));
    }

    @GetMapping("/getAll/{employeeUuid}")
    public ResponseEntity<List<FeedbackResponseDto>> getAllFeedbacks(@PathVariable UUID employeeUuid) {
        return ResponseEntity.ok(feedbackService.getAll(employeeUuid));
    }

    @GetMapping("/getFeedback/{employeeUuid}")
    public ResponseEntity<List<FeedbackResponseDto>> getFeedback(@PathVariable UUID employeeUuid,
                                                                 @RequestParam FeedbackType feedbackType) {
        return ResponseEntity.ok(feedbackService.getFeedback(employeeUuid, feedbackType));
    }

    @PostMapping("/send")
    public ResponseEntity<Feedback> addFeedback(@RequestBody Feedback feedback) {
        return new ResponseEntity<>(feedbackService.send(feedback), HttpStatus.CREATED);
    }

    @PostMapping("/request")
    public ResponseEntity<Feedback> requestFeedback(@RequestBody RequestFeedbackDto requestFeedbackDto) {
        return new ResponseEntity<>(feedbackService.requestFeedback(requestFeedbackDto), HttpStatus.CREATED);
    }


    @PutMapping("/update/{feedbackId}")
    public ResponseEntity<Feedback> update(@PathVariable UUID feedbackId, @RequestBody Feedback feedback) {
        return new ResponseEntity<>(feedbackService.update(feedbackId, feedback), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete/{feedbackId}")
    public ResponseEntity<HttpStatus> deleteFeedback(@PathVariable UUID feedbackId) {
        feedbackService.delete(feedbackId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
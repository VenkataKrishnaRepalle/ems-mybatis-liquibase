package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.entity.Feedback;
import com.learning.emsmybatisliquibase.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

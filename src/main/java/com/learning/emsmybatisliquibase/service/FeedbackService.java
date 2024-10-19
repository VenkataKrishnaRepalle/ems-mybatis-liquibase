package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.entity.Feedback;

import java.util.UUID;

public interface FeedbackService {
    Feedback getById(UUID uuid);
}

package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.FeedbackDao;
import com.learning.emsmybatisliquibase.entity.Feedback;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackDao feedbackDao;

    @Override
    public Feedback getById(UUID uuid) {
        var feedback = feedbackDao.getById(uuid);
        if (feedback == null) {
            throw new NotFoundException("FEEDBACK_NOT_FOUND", "Feedback not found with uuid: " + uuid);
        }
        return feedback;
    }
}

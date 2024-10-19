package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.FeedbackDao;
import com.learning.emsmybatisliquibase.entity.Feedback;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    @InjectMocks
    FeedbackServiceImpl feedbackService;

    @Mock
    FeedbackDao feedbackDao;

    @Test
    void getById_success() {
        var uuid = UUID.randomUUID();
        var feedback = new Feedback();
        feedback.setUuid(uuid);

        when(feedbackDao.getById(uuid)).thenReturn(feedback);

        var result = feedbackService.getById(uuid);

        assertEquals(feedback, result);
        verify(feedbackDao, times(1)).getById(uuid);
    }

    @Test
    void getById_notFound() {
        when(feedbackDao.getById(any(UUID.class))).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                feedbackService.getById(UUID.randomUUID()));
    }

}
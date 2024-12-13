package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.entity.Employee;
import com.learning.emsmybatisliquibase.entity.ReviewType;

public interface NotificationService {

    void sendSuccessfulEmployeeOnBoard(Employee employee, String password, int capacity);

    void sendNotificationBeforeStart(ReviewType reviewType);

    void sendStartNotification(ReviewType startedReviewType);
}

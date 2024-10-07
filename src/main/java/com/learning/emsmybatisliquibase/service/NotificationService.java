package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.entity.ReviewType;

import java.util.UUID;

public interface NotificationService {

    void sendSuccessfulEmployeeOnBoard(UUID employeeUuid, String password);

    void sendNotificationBeforeStart(ReviewType reviewType);
}

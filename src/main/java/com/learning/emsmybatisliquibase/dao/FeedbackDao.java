package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Feedback;
import com.learning.emsmybatisliquibase.entity.FeedbackType;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface FeedbackDao {

    int insert(@Param("feedback") Feedback feedback);

    int update(@Param("feedback") Feedback feedback);

    List<Feedback> findSendFeedback(@Param("employeeUuid") UUID employeeUuid, @Param("type") FeedbackType type);

    List<Feedback> findReceivedFeedback(@Param("targetEmployeeUuid") UUID targetEmployeeUuid, @Param("type") FeedbackType type);

    List<Feedback> getAll(@Param("employeeUuid") UUID employeeUuid);

    Feedback getById(@Param("uuid") UUID uuid);

    int delete(@Param("uuid") UUID uuid);
}

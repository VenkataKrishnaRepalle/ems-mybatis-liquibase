package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.dto.NotificationDto;
import com.learning.emsmybatisliquibase.entity.PeriodStatus;
import com.learning.emsmybatisliquibase.entity.ReviewTimeline;
import com.learning.emsmybatisliquibase.entity.ReviewType;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface ReviewTimelineDao {

    List<ReviewTimeline> getByEmployeeCycleId(@Param("employeeCycleUuid") UUID employeeCycleId);

    int insert(@Param("reviewTimeline") ReviewTimeline reviewTimeline);

    int update(@Param("reviewTimeline") ReviewTimeline reviewTimeline);

    List<ReviewTimeline> findByStatusAndReviewType(@Param("status") PeriodStatus status, @Param("reviewType") ReviewType reviewType);

    ReviewTimeline getById(@Param("uuid") UUID uuid);

    List<ReviewTimeline> getByEmployeeUuidsAndReviewType(@Param("employeeUuids") List<UUID> employeeUuids, @Param("reviewType") ReviewType reviewType);

    List<NotificationDto> getTimelineIdsByReviewType(@Param("reviewType") ReviewType reviewType);
}

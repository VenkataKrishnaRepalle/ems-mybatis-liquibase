package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.dto.NotificationDto;
import com.learning.emsmybatisliquibase.entity.CycleStatus;
import com.learning.emsmybatisliquibase.entity.ReviewType;
import com.learning.emsmybatisliquibase.entity.Timeline;
import org.apache.ibatis.annotations.Param;

import java.sql.Time;
import java.util.List;
import java.util.UUID;

public interface TimelineDao {

    List<Timeline> getByEmployeeCycleId(@Param("employeeCycleUuid") UUID employeeCycleId);

    int insert(@Param("timeline") Timeline timeline);

    int update(@Param("timeline") Timeline timeline);

    List<Timeline> findByStatusAndReviewType(@Param("status") CycleStatus status, @Param("reviewType") ReviewType reviewType);

    Timeline getById(@Param("uuid") UUID uuid);

    List<Timeline> getByEmployeeUuidsAndReviewType(@Param("employeeUuids") List<UUID> employeeUuids, @Param("reviewType") ReviewType reviewType);

    List<NotificationDto> getTimelineIdsByReviewType(@Param("reviewType") ReviewType reviewType);
}

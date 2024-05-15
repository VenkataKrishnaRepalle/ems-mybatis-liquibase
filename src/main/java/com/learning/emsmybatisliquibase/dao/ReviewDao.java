package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Review;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

public interface ReviewDao {
    Review getByTimelineId(@Param("timelineId") UUID timelineUuid);

    int insert(@Param("review") Review review);

    int update(@Param("review") Review review);

    int delete(@Param("reviewId") UUID reviewUuid);

    Review getById(@Param("id") UUID reviewUuid);
}

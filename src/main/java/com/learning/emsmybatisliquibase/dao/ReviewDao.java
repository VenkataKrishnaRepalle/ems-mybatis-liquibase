package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.dto.AddEmployeeReviewResponseDto;
import org.apache.ibatis.annotations.Param;

public interface ReviewDao {
    int insertByEmployee(@Param("reviewDto") AddEmployeeReviewResponseDto reviewDto);
}

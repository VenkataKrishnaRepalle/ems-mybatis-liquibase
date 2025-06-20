package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Period;
import com.learning.emsmybatisliquibase.entity.enums.PeriodStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface PeriodDao {

    Period getById(@Param("uuid") UUID uuid);

    Period getByStatus(@Param("status") PeriodStatus status);

    List<Period> getAll();

    int insert(@Param("period") Period period);

    int update(@Param("period") Period period);

    Period getByYear(@Param("year") long year);
}

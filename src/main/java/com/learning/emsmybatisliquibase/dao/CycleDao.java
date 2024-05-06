package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Cycle;
import com.learning.emsmybatisliquibase.entity.CycleStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface CycleDao {

    Cycle getById(@Param("uuid") UUID uuid);

    Cycle getByStatus(@Param("status") CycleStatus status);

    List<Cycle> getAll();

    int insert(@Param("cycle") Cycle cycle);

    int update(@Param("cycle") Cycle cycle);
}

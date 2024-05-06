package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.Cycle;
import com.learning.emsmybatisliquibase.entity.CycleStatus;

import java.util.UUID;

public interface CycleService {

    Cycle createCycle(int year);

    SuccessResponseDto startScheduled(UUID cycleId);

    Cycle getById(UUID cycleId);

    SuccessResponseDto updateStatus(UUID cycleId, CycleStatus status);

    Cycle getCurrentActiveCycle();
}

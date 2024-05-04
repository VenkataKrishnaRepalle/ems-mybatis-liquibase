package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.AttendanceStatus;
import com.learning.emsmybatisliquibase.entity.AttendanceType;
import com.learning.emsmybatisliquibase.entity.WorkMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAttendanceDto {

    private UUID uuid;

    private WorkMode workMode;

    private AttendanceType type;

    private AttendanceStatus status;
}

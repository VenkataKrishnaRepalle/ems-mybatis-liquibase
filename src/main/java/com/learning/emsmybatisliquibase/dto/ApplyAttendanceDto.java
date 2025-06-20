package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.enums.AttendanceStatus;
import com.learning.emsmybatisliquibase.entity.enums.AttendanceType;
import com.learning.emsmybatisliquibase.entity.enums.WorkMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyAttendanceDto {

    private WorkMode workMode;

    private AttendanceType type;

    private AttendanceStatus status;

    private Date date;
}

package com.learning.emsmybatisliquibase.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attendance implements Serializable {

    private UUID uuid;

    private UUID employeeUuid;

    private WorkMode workMode;

    private AttendanceShiftType shiftType;

    private AttendanceType attendanceType;

    private AttendanceStatus attendanceStatus;

    private Date attendanceDate;

    private Instant creationTime;

    private Instant updatedTime;
}

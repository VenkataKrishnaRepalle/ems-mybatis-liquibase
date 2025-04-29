package com.learning.emsmybatisliquibase.mapper;

import com.learning.emsmybatisliquibase.dto.ApplyAttendanceDto;
import com.learning.emsmybatisliquibase.entity.Attendance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    Attendance applyAttendanceDtoToAttendance(ApplyAttendanceDto attendance);
}

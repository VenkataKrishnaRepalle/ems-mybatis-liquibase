package com.learning.emsmybatisliquibase.mapper;

import com.learning.emsmybatisliquibase.dto.ApplyLeaveDto;
import com.learning.emsmybatisliquibase.entity.Leave;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface LeaveMapper {
    List<Leave> applyLeaveDtoToLeaveDay(List<ApplyLeaveDto> leaveDto);
}

package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.LeaveType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyLeaveDto {

    @Temporal(TemporalType.DATE)
    private Date date;

    private LeaveType type;

    private String reason;
}

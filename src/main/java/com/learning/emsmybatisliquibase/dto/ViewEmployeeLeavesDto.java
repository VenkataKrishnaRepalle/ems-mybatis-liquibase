package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.Leave;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewEmployeeLeavesDto {
    private UUID employeeUuid;

    private String employeeFirstName;

    private String employeeLastName;

    private List<Leave> pastLeaves;

    private List<Leave> futureLeaves;

    private List<Leave> approvedLeaves;

    private List<Leave> declinedLeaves;

    private List<Leave> waitingForApprovalLeaves;

    private List<Leave> cancelledLeaves;
}

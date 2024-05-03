package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.*;
import com.learning.emsmybatisliquibase.entity.Leave;
import com.learning.emsmybatisliquibase.entity.LeaveStatus;
import com.learning.emsmybatisliquibase.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping("/applyLeave/{employeeId}")
    public ResponseEntity<List<Leave>> add(@PathVariable UUID employeeId, @RequestBody List<ApplyLeaveDto> applyLeaveDto) {
        return new ResponseEntity<>(leaveService.applyLeave(employeeId, applyLeaveDto), HttpStatus.CREATED);
    }

    @GetMapping("/get-leaves/{employeeId}")
    public ResponseEntity<ViewEmployeeLeavesDto> getLeavesByEmployeeId(@PathVariable UUID employeeId) {
        return new ResponseEntity<>(leaveService.getLeavesByEmployeeId(employeeId), HttpStatus.OK);
    }

    @PostMapping("/parse-leave")
    public ResponseEntity<List<ParseLeaveResponseDto>> parseLeave(@RequestBody ParseLeaveRequestDto parseLeaveDto) {
        return new ResponseEntity<>(leaveService.parseLeave(parseLeaveDto), HttpStatus.OK);
    }

    @GetMapping("/get-leaves/manager/{managerUuid}")
    public ResponseEntity<List<ViewEmployeeLeavesDto>> getAllEmployeesLeavesByManager(@PathVariable UUID managerUuid) {
        return new ResponseEntity<>(leaveService.getAllEmployeesLeavesByManager(managerUuid), HttpStatus.OK);
    }

    @PostMapping("/update-leaves/manager/{managerUuid}")
    public ResponseEntity<SuccessResponseDto> updateLeavesByManager(@PathVariable UUID managerUuid,
                                                                    @RequestParam LeaveStatus status,
                                                                    @RequestBody List<UpdateLeaveByManagerDto> leavesDto) {
        return new ResponseEntity<>(leaveService.updateLeavesByManager(managerUuid, status, leavesDto), HttpStatus.ACCEPTED);
    }
}

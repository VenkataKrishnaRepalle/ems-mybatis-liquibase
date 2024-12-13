package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.entity.Experience;
import com.learning.emsmybatisliquibase.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("experience")
public class ExperienceController {

    private final ExperienceService experienceService;

    @GetMapping("/getById/{experienceId}")
    public ResponseEntity<Experience> getById(@PathVariable UUID experienceId) {
        return new ResponseEntity<>(experienceService.getById(experienceId),
                HttpStatus.OK);
    }

    @GetMapping("/getAll/employee/{employeeId}")
    public ResponseEntity<List<Experience>> getAllByEmployeeUuid(@PathVariable UUID employeeId) {
        return new ResponseEntity<>(experienceService.getAllByEmployeeUuid(employeeId),
                HttpStatus.OK);
    }

    @PostMapping("/add/employee/{employeeId}")
    public ResponseEntity<List<Experience>> add(@PathVariable UUID employeeId,
                                                @RequestBody List<Experience> experiences) {
        return new ResponseEntity<>(experienceService.add(employeeId, experiences),
                HttpStatus.CREATED);
    }

    @PutMapping("/update/employee/{employeeId}")
    public ResponseEntity<List<Experience>> update(@PathVariable UUID employeeId,
                                                   @RequestBody List<Experience> experiences) {
        return new ResponseEntity<>(experienceService.update(employeeId, experiences),
                HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/deleteById/{experienceId}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable UUID experienceId) {
        experienceService.deleteById(experienceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

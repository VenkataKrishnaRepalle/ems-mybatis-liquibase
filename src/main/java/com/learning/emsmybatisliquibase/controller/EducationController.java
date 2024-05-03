package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.entity.Education;
import com.learning.emsmybatisliquibase.service.EducationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/education")
@AllArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @PostMapping("/addEducation")
    public ResponseEntity<Education> add(@RequestBody Education educationDto) {
        return new ResponseEntity<>(educationService.save(educationDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @PutMapping("/updateEducation/{id}")
    public ResponseEntity<Education> update(@RequestBody Education educationDto, @PathVariable UUID id) {
        return new ResponseEntity<>(educationService.update(educationDto, id), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @GetMapping("/getEducationDetails/{id}")
    public ResponseEntity<Education> get(@PathVariable UUID id) {
        return new ResponseEntity<>(educationService.getById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @GetMapping("/getAllEducationDetails/{employeeId}")
    public ResponseEntity<List<Education>> getAll(@PathVariable UUID employeeId) {
        return new ResponseEntity<>(educationService.getAll(employeeId), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @DeleteMapping("deleteEducationDetails/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable UUID id) {
        educationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.SkillsDto;
import com.learning.emsmybatisliquibase.entity.Skills;
import com.learning.emsmybatisliquibase.service.SkillsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/skills")
public class SkillsController {

    private final SkillsService skillsService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @PostMapping("/add-skills")
    public ResponseEntity<Skills> add(@RequestBody SkillsDto skillsDto) {
        return new ResponseEntity<>(skillsService.add(skillsDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @PutMapping("/update-skills{skillsUuid}")
    public ResponseEntity<Skills> update(@PathVariable UUID skillsUuid, @RequestBody SkillsDto skillsDto) {
        return new ResponseEntity<>(skillsService.update(skillsUuid, skillsDto), HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/get-skills-by-id/{skillsUuid}")
    public ResponseEntity<Skills> gteById(@PathVariable UUID skillsUuid) {
        return new ResponseEntity<>(skillsService.getById(skillsUuid), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/get-all-skills-by-employeeUuid/{employeeUuid}")
    public ResponseEntity<List<Skills>> getAllBYColleagueUuid(@PathVariable UUID employeeUuid) {
        return new ResponseEntity<>(skillsService.getAll(employeeUuid), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @DeleteMapping("/delete-skills-by-id/{skillsUuid}/employee/{employeeUuid}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable UUID employeeUuid, @PathVariable UUID skillsUuid) {
        skillsService.deleteById(skillsUuid, employeeUuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

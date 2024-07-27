package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.CertificationRequestDto;
import com.learning.emsmybatisliquibase.dto.CertificationResponseDto;
import com.learning.emsmybatisliquibase.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certification")
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping("/getAll/{employeeUuid}")
    public ResponseEntity<List<CertificationResponseDto>> getAllByEmployeeUuid(@PathVariable UUID employeeUuid) {
        return new ResponseEntity<>(certificationService.getAllByEmployeeUuid(employeeUuid), HttpStatus.OK);
    }

    @GetMapping("/get/{certificationUuid}")
    public ResponseEntity<CertificationResponseDto> getById(@PathVariable UUID certificationUuid) {
        return new ResponseEntity<>(certificationService.getById(certificationUuid), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<CertificationResponseDto> add(@RequestBody CertificationRequestDto addCertificationDto) {
        return new ResponseEntity<>(certificationService.add(addCertificationDto), HttpStatus.CREATED);
    }

    @PutMapping("/update/{certificationUuid}")
    public ResponseEntity<CertificationResponseDto> update(@PathVariable UUID certificationUuid, @RequestBody CertificationRequestDto updateCertificationDto) {
        return new ResponseEntity<>(certificationService.update(certificationUuid, updateCertificationDto), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete/{certificationUuid}")
    public ResponseEntity<HttpStatus> delete(@PathVariable UUID certificationUuid) {
        certificationService.delete(certificationUuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getByCertificationCategory/{certificationCategoryUuid}")
    public ResponseEntity<Map<UUID, String>> getByCertificationCategory(@PathVariable UUID certificationCategoryUuid) {
        return new ResponseEntity<>(certificationService.getByCertificationCategory(certificationCategoryUuid), HttpStatus.OK);
    }
}

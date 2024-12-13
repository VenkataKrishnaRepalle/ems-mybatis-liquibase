package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.CertificationCategoryDto;
import com.learning.emsmybatisliquibase.entity.CertificationCategory;
import com.learning.emsmybatisliquibase.service.CertificationCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/certification-category")
@RequiredArgsConstructor
public class CertificationCategoryController {

    private final CertificationCategoryService certificationCategoryService;

    @GetMapping("get/{id}")
    public ResponseEntity<CertificationCategory> getById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(certificationCategoryService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<CertificationCategory>> getAll() {
        return new ResponseEntity<>(certificationCategoryService.getAll(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<CertificationCategory> add(@RequestBody CertificationCategoryDto certificationCategoryDto) {
        return new ResponseEntity<>(certificationCategoryService.add(certificationCategoryDto),
                HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CertificationCategory> update(@RequestBody @PathVariable UUID id,
                                                        @RequestBody CertificationCategory certificationCategory) {
        return new ResponseEntity<>(certificationCategoryService.update(id, certificationCategory),
                HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") UUID id) {
        certificationCategoryService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

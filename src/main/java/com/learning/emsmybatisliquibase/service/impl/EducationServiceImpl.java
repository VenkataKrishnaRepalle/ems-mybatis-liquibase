package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.EducationDao;
import com.learning.emsmybatisliquibase.entity.Education;
import com.learning.emsmybatisliquibase.entity.EducationDegree;
import com.learning.emsmybatisliquibase.exception.AlreadyFoundException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.service.EducationService;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationDao educationDao;

    private final EmployeeService employeeService;

    @Override
    public Education save(Education educationDto) {
        employeeService.getById(educationDto.getEmployeeUuid());

        var educations = educationDao.getAllByEmployeeUuid(educationDto.getEmployeeUuid());
        var degree = educationDto.getDegree();

        if (educations == null) {
            if (degree != EducationDegree.SSC_10TH) {
                throw new NotFoundException("Education Details of " + EducationDegree.SSC_10TH + " not found");
            }
        } else {
            var isDegreePresent = educations.stream()
                    .filter(education -> education.getDegree().equals(educationDto.getDegree())).findFirst();
            if (isDegreePresent.isPresent()) {
                throw new AlreadyFoundException("Degree already exists: " + degree);
            }
        }
        educationDto.setUuid(UUID.randomUUID());
        educationDto.setCreatedTime(Instant.now());
        educationDto.setUpdatedTime(Instant.now());
        educationDao.insert(educationDto);
        return educationDto;
    }

    @Override
    public Education update(Education educationDto, UUID id) {
        var education = getById(educationDto.getUuid());
        education.setEmployeeUuid(educationDto.getEmployeeUuid());
        education.setDegree(educationDto.getDegree());
        education.setSchoolName(educationDto.getSchoolName());
        education.setStartDate(educationDto.getStartDate());
        education.setEndDate(educationDto.getEndDate());
        education.setUpdatedTime(Instant.now());

        educationDao.update(educationDto);

        return education;
    }

    @Override
    public Education getById(UUID id) {
        var education = educationDao.get(id);
        if (education == null) {
            throw new NotFoundException("Education details not found with id: " + id);
        }
        return education;
    }

    @Override
    public List<Education> getAll(UUID employeeId) {
        List<Education> educations = educationDao.getAllByEmployeeUuid(employeeId);
        if (educations == null) {
            throw new NotFoundException("Education details not found");
        }
        return educations;
    }

    @Override
    public void deleteById(UUID id) {
        getById(id);
        educationDao.delete(id);
    }
}

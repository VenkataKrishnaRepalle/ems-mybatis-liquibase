package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.ExperienceDao;
import com.learning.emsmybatisliquibase.entity.Experience;
import com.learning.emsmybatisliquibase.exception.FoundException;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceDao experienceDao;

    @Override
    public Experience getById(UUID experienceId) {
        var experience = experienceDao.getById(experienceId);
        if (experience == null) {
            throw new NotFoundException("EXPERIENCE_NOT_FOUND", "experience not found with id: " + experienceId);
        }
        return experience;
    }

    @Override
    public List<Experience> getAllByEmployeeUuid(UUID employeeId) {
        return experienceDao.getByEmployeeUuid(employeeId);
    }

    @Override
    public List<Experience> add(UUID employeeUuid, List<Experience> experiences) {
        if (experiences == null) {
            throw new InvalidInputException("EXPERIENCE_INPUT_NULL", "experiences input is null");
        }

        isInputOverlapping(experiences);
        isExistingExperienceOverlappingInputExperience(employeeUuid, experiences);

        experiences.forEach(experience -> {
            experience.setUuid(UUID.randomUUID());
            if (Boolean.TRUE.equals(experience.getIsCurrentJob())) {
                experience.setEndDate(null);
            } else if (experience.getEndDate() != null) {
                experience.setIsCurrentJob(Boolean.FALSE);
            }
            if (0 == experienceDao.insert(experience)) {
                throw new IntegrityException("EXPERIENCE_NOT_INSERTED", "insertion failed");
            }
        });

        return experiences;
    }

    @Override
    public List<Experience> update(UUID employeeUuid, List<Experience> experiences) {
        if (experiences == null) {
            throw new InvalidInputException("EXPERIENCE_INPUT_NULL", "experiences input is null");
        }

        experiences.forEach(experience -> getById(experience.getUuid()));

        isInputOverlapping(experiences);

        experiences.forEach(experience -> {
            if (Boolean.TRUE.equals(experience.getIsCurrentJob())) {
                experience.setEndDate(null);
            } else if (experience.getEndDate() != null) {
                experience.setIsCurrentJob(Boolean.FALSE);
            }
            if (0 == experienceDao.update(experience)) {
                throw new IntegrityException("EXPERIENCE_NOT_UPDATED", "experience not updated with id: " + experience.getUuid());
            }
        });

        return experiences;
    }

    @Override
    public void deleteById(UUID experienceId) {
        getById(experienceId);
        if (0 == experienceDao.delete(experienceId)) {
            throw new IntegrityException("EXPERIENCE_NOT_DELETED", "experience not deleted with id: " + experienceId);
        }
        var isExperienceDeleted = experienceDao.getById(experienceId);
        if (isExperienceDeleted != null) {
            throw new FoundException("EXPERIENCE_NOT_DELETED", "experience not deleted with id: " + experienceId);
        }
    }

    private void isInputOverlapping(List<Experience> experiences) {
        for (int i = 0; i < experiences.size() - 1; i++) {
            for (int j = i + 1; j < experiences.size(); j++) {
                if (isOverlapping(experiences.get(i), experiences.get(j))) {
                    throw new InvalidInputException("EXPERIENCE_INPUT_OVERLAPPING",
                            "experience date is overlapping, Please check and update");
                }
            }
        }
    }

    private void isExistingExperienceOverlappingInputExperience(UUID employeeUuid, List<Experience> experiences) {
        var employeeExperiences = experienceDao.getByEmployeeUuid(employeeUuid);
        if (employeeExperiences != null) {
            for (Experience experience : experiences) {
                boolean isOverlapping = employeeExperiences.stream().anyMatch(existingExperience ->
                        isOverlapping(experience, existingExperience)
                );
                if (isOverlapping) {
                    throw new InvalidInputException("EXPERIENCE_OVERLAPPING",
                            "input experience dates are overlapping, please check and Update");
                }
            }
        }
    }

    private boolean isOverlapping(Experience newExperience, Experience existingExperience) {
        Instant newStart = newExperience.getStartDate().toInstant();
        Instant newEnd;
        if (Boolean.TRUE.equals(newExperience.getIsCurrentJob())) {
            newEnd = Instant.now();
        } else {
            newEnd = newExperience.getEndDate().toInstant();
        }
        Instant existingStart = existingExperience.getStartDate().toInstant();
        Instant existingEnd;

        if (Boolean.TRUE.equals(existingExperience.getIsCurrentJob())) {
            existingEnd = Instant.now();
        } else {
            existingEnd = existingExperience.getEndDate().toInstant();
        }

        return (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart));
    }
}

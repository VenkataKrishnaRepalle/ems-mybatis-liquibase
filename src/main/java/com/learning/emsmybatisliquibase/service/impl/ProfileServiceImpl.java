package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.ProfileDao;
import com.learning.emsmybatisliquibase.entity.Profile;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.learning.emsmybatisliquibase.exception.errorcodes.ProfileErrorCodes.*;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileDao profileDao;

    public Profile getByEmployeeUuid(UUID employeeUuid) {
        var profile = profileDao.get(employeeUuid);
        if (profile == null) {
            throw new NotFoundException(PROFILE_NOT_FOUND.code(),
                    "Profile not found for colleague id: " + employeeUuid);
        }
        return profile;
    }


    @Override
    public Profile insert(Profile profile) {
        try {
            if (0 == profileDao.insert(profile)) {
                throw new NotFoundException(PROFILE_NOT_CREATED.code(),
                        "Failed in saving profile");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(PROFILE_NOT_CREATED.code(),
                    exception.getCause().getMessage());
        }
        return profile;
    }

    @Override
    public Profile update(Profile profileInput) {
        getByEmployeeUuid(profileInput.getEmployeeUuid());

        try {
            if (0 == profileDao.update(profileInput)) {
                throw new IntegrityException(PROFILE_NOT_UPDATED.code(),
                        "Profile not updated");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(PROFILE_NOT_UPDATED.code(),
                    exception.getCause().getMessage());
        }

        return profileInput;
    }
}

package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.PasswordDao;
import com.learning.emsmybatisliquibase.dto.PasswordDto;
import com.learning.emsmybatisliquibase.entity.Password;
import com.learning.emsmybatisliquibase.entity.PasswordStatus;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.service.PasswordService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.*;

@Service
@AllArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final PasswordDao passwordDao;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Password getById(UUID uuid) {
        var password = passwordDao.getById(uuid);
        if (password == null) {
            throw new IntegrityException("PASSWORD_NOT_FOUND", "Password not found with id: " + uuid);
        }
        return password;
    }

    @Override
    public Password create(UUID employeeUuid, PasswordDto passwordDto) {
        validatePassword(passwordDto.getPassword(), passwordDto.getConfirmPassword());

        return insert(employeeUuid, passwordDto.getPassword());
    }

    private Password insert(UUID employeeUuid, String passwordInput) {
        var password = Password.builder()
                .uuid(UUID.randomUUID())
                .employeeUuid(employeeUuid)
                .password(passwordEncoder.encode(passwordInput))
                .status(PasswordStatus.ACTIVE)
                .createdTime(Instant.now())
                .updatedTime(Instant.now())
                .build();

        try {
            if (0 == passwordDao.insert(password)) {
                throw new IntegrityException("PASSWORD_NOT_INSERTED", "Password failed to create");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException("PASSWORD_NOT_INSERTED", "Password failed to create");
        }
        return password;
    }

    private void validatePassword(String password, String confirmPassword) {
        if (StringUtils.isNotEmpty(password) && password.equals(confirmPassword)) {
            if (password.length() < 8) {
                throw new InvalidInputException(EMPLOYEE_PASSWORD_LENGTH_TOO_SHORT.code(),
                        "Password length should be at least 8 characters");
            }
        } else {
            throw new InvalidInputException(EMPLOYEE_PASSWORD_MISMATCH.code(),
                    "Password and Confirm Password should be the same");
        }
    }

    @Override
    public Password update(Password password) {
        getById(password.getUuid());

        try {
            if (0 == passwordDao.update(password)) {
                throw new IntegrityException("PASSWORD_UPDATE_FAILED", "Password not updated");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException("PASSWORD_UPDATE_FAILED", "Password not updated");
        }

        return password;
    }


}

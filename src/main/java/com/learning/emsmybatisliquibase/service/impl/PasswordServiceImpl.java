package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.PasswordDao;
import com.learning.emsmybatisliquibase.entity.Password;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.service.PasswordService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

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
    public Password create(UUID employeeUuid, String passwordInput) {
        var password = Password.builder()
                .uuid(UUID.randomUUID())
                .employeeUuid(employeeUuid)
                .password(passwordEncoder.encode(passwordInput))
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

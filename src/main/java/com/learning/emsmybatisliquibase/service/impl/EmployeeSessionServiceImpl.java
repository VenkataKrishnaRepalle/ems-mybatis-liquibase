package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.EmployeeSessionDao;
import com.learning.emsmybatisliquibase.entity.EmployeeSession;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.service.EmployeeSessionService;
import com.learning.emsmybatisliquibase.utils.ErrorMessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeSessionErrorCodes.*;

@Service
@RequiredArgsConstructor
public class EmployeeSessionServiceImpl implements EmployeeSessionService {

    private final EmployeeSessionDao employeeSessionDao;

    private static final String ACTIVE = "active";

    private static final String INACTIVE = "inactive";

    @Override
    public Map<String, List<EmployeeSession>> getByEmployeeUuid(UUID employeeUuid, Boolean isActive) {
        if (null != isActive) {
            var activeMessage = isActive ? ACTIVE : INACTIVE;
            return Map.of(activeMessage, employeeSessionDao.getByEmployeeUuidAndStatus(employeeUuid, isActive));
        }
        return Map.of(ACTIVE, employeeSessionDao.getByEmployeeUuidAndStatus(employeeUuid, true),
                INACTIVE, employeeSessionDao.getByEmployeeUuidAndStatus(employeeUuid, false));
    }

    @Override
    public EmployeeSession update(UUID employeeUuid, EmployeeSession employeeSession) {
        var existingSession = employeeSessionDao.getById(employeeSession.getUuid());
        if (null == existingSession) {
            throw new NotFoundException(EMPLOYEE_SESSION_NOT_FOUND.code(),
                    ErrorMessageUtil.getMessage(EMPLOYEE_SESSION_NOT_FOUND.code(), employeeSession.getUuid()));
        }
        try {
            if (0 == employeeSessionDao.update(employeeSession)) {
                throw new IntegrityException(EMPLOYEE_SESSION_UPDATE_FAILED.code(),
                        ErrorMessageUtil.getMessage(EMPLOYEE_SESSION_UPDATE_FAILED.code(), employeeSession.getUuid()));
            }
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityException(EMPLOYEE_SESSION_UPDATE_FAILED.code(),
                    e.getCause().getMessage());
        }
        return employeeSession;
    }

    @Override
    public void delete(UUID sessionUuid) {
        var existingSession = employeeSessionDao.getById(sessionUuid);
        if (null == existingSession) {
            throw new NotFoundException(EMPLOYEE_SESSION_NOT_FOUND.code(),
                    ErrorMessageUtil.getMessage(EMPLOYEE_SESSION_NOT_FOUND.code(), sessionUuid));
        }
        try {
            if (0 == employeeSessionDao.delete(sessionUuid)) {
                throw new IntegrityException(EMPLOYEE_SESSION_DELETE_FAILED.code(),
                        ErrorMessageUtil.getMessage(EMPLOYEE_SESSION_DELETE_FAILED.code(), sessionUuid));
            }
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityException(EMPLOYEE_SESSION_DELETE_FAILED.code(),
                    e.getCause().getMessage());
        }
    }
}

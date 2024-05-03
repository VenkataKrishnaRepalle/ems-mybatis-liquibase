package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum EmployeeErrorCodes implements ErrorCodeAware {
    EMPLOYEE_NOT_FOUND,
    EMPLOYEE_ALREADY_EXISTS,
    PASSWORD_NOT_MATCHED,
    EMPLOYEE_NOT_CREATED,
    EMPLOYEE_INTEGRATE_VIOLATION,
    INVALID_INPUT_EXCEPTION,
    INVALID_MANAGER_PROVIDED,
    INVALID_MANGER_ACCESS,
    MANAGER_ACCESS_NOT_FOUND;

    @Override
    public String code() {
        return name();
    }
}

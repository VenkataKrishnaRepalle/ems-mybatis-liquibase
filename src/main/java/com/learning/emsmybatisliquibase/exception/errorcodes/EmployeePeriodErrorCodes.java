package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum EmployeePeriodErrorCodes implements ErrorCodeAware {
    EMPLOYEE_CYCLE_NOT_FOUND,
    EMPLOYEE_CYCLE_NOT_CREATED,
    EMPLOYEE_CYCLE_NOT_UPDATED,
    EMPLOYEE_CYCLE_ALREADY_EXISTS;

    @Override
    public String code() {
        return name();
    }
}

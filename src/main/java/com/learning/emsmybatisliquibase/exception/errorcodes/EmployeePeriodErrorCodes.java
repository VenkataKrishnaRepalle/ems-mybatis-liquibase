package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum EmployeePeriodErrorCodes implements ErrorCodeAware {
    EMPLOYEE_PERIOD_NOT_FOUND,
    EMPLOYEE_PERIOD_NOT_CREATED,
    EMPLOYEE_PERIOD_NOT_UPDATED,
    EMPLOYEE_PERIOD_ALREADY_EXISTS;

    @Override
    public String code() {
        return name();
    }
}

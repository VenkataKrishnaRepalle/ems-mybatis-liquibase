package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum EmployeeRoleErrorCodes implements ErrorCodeAware {
    EMPLOYEE_ROLE_ALREADY_EXISTS,
    EMPLOYEE_ROLE_NOT_CREATED,
    EMPLOYEE_ROLE_NOT_DELETED;

    @Override
    public String code() {
        return name();
    }
}

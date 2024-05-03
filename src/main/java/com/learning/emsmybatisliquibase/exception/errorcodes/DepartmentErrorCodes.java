package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum DepartmentErrorCodes implements ErrorCodeAware {
    DEPARTMENT_ALREADY_EXIST,
    DEPARTMENT_NOT_FOUND,
    DEPARTMENT_NOT_CREATED;

    @Override
    public String code() {
        return name();
    }
}

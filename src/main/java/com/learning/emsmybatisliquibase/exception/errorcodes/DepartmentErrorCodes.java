package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum DepartmentErrorCodes implements ErrorCodeAware {
    DEPARTMENT_ALREADY_EXIST,
    DEPARTMENT_NOT_FOUND,
    DEPARTMENT_NOT_CREATED,
    DEPARTMENT_NOT_UPDATED,
    DEPARTMENT_NOT_DELETED,
    PROFILE_NOT_UPDATED,
    DEPARTMENT_NAME_ALREADY_EXISTS;

    @Override
    public String code() {
        return name();
    }
}

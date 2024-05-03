package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum EducationErrorCodes implements ErrorCodeAware {
    EDUCATION_DETAILS_NOT_FOUND,
    EDUCATION_DEGREE_ALREADY_EXISTS;

    @Override
    public String code() {
        return name();
    }
}

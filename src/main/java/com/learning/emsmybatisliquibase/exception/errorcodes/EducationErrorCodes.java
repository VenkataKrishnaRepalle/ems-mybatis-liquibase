package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum EducationErrorCodes implements ErrorCodeAware {
    EDUCATION_DETAILS_NOT_FOUND,
    EDUCATION_DEGREE_ALREADY_EXISTS,
    EDUCATION_NOT_UPDATED,
    EDUCATION_NOT_CREATED,
    EDUCATION_NOT_DELETED;

    @Override
    public String code() {
        return name();
    }
}

package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum ExperienceErrorCodes implements ErrorCodeAware {
    EXPERIENCE_NOT_FOUND,
    EXPERIENCE_INPUT_NULL,
    EXPERIENCE_NOT_UPDATED,
    EXPERIENCE_NOT_CREATED,
    EXPERIENCE_NOT_DELETED,
    EXPERIENCE_OVERLAPPING,
    EXPERIENCE_INPUT_OVERLAPPING;

    @Override
    public String code() {
        return name();
    }
}

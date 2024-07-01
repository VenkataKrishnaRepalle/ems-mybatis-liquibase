package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum ReviewErrorCodes implements ErrorCodeAware {
    REVIEW_ALREADY_EXISTS,
    REVIEW_NOT_EXISTS,
    REVIEW_NOT_CREATED,
    REVIEW_NOT_UPDATED,
    REVIEW_NOT_DELETED;

    @Override
    public String code() {
        return name();
    }
}

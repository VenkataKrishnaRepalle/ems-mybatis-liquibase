package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum ProfileErrorCodes implements ErrorCodeAware {
    PROFILE_NOT_CREATED,
    PROFILE_NOT_UPDATED,
    PROFILE_NOT_FOUND;

    @Override
    public String code() {
        return name();
    }
}

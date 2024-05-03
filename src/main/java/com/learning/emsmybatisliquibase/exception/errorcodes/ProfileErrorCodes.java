package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum ProfileErrorCodes implements ErrorCodeAware {
    PROFILE_NOT_CREATED;

    @Override
    public String code() {
        return name();
    }
}

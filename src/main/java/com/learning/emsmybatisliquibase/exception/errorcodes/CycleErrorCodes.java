package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum CycleErrorCodes implements ErrorCodeAware {
    CYCLE_ALREADY_EXISTS,
    CYCLE_NOT_EXISTS,
    CYCLE_NOT_CREATED,
    CYCLE_NOT_UPDATED;

    @Override
    public String code() {
        return name();
    }
}

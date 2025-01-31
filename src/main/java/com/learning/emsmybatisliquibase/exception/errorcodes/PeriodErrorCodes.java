package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum PeriodErrorCodes implements ErrorCodeAware {
    PERIOD_ALREADY_EXISTS,
    PERIOD_NOT_EXISTS,
    PERIOD_NOT_CREATED,
    PERIOD_NOT_UPDATED,
    PERIOD_COMPLETED;

    @Override
    public String code() {
        return name();
    }
}

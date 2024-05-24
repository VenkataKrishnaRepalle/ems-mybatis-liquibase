package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum TimelineErrorCodes implements ErrorCodeAware {
    TIMELINE_NOT_FOUND;

    @Override
    public String code() {
        return "";
    }
}

package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum TimelineErrorCodes implements ErrorCodeAware {
    TIMELINE_NOT_FOUND,
    TIMELINE_NOT_STARTED,
    TIMELINE_NOT_CREATED,
    TIMELINE_NOT_DELETED,
    TIMELINE_NOT_ASSIGNED,
    TIMELINE_NOT_UPDATED,
    TIMELINE_LOCKED,
    TIMELINE_COMPLETED,
    TIMELINE_ALREADY_EXIST;

    @Override
    public String code() {
        return name();
    }
}

package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum FileErrorCodes implements ErrorCodeAware {
    SHEET_NOT_FOUND,
    INVALID_COLUMN_HEADINGS;

    @Override
    public String code() {
        return name();
    }
}

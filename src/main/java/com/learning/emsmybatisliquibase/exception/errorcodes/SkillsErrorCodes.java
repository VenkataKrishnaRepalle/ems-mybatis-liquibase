package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum SkillsErrorCodes implements ErrorCodeAware {
    SKILLS_NOT_EXISTS,
    SKILLS_ALREADY_EXISTS,
    SKILLS_NOT_CREATED,
    SKILLS_NOT_UPDATED,
    SKILLS_NOT_DELETED,
    INVALID_RATINGS_INPUT;

    @Override
    public String code() {
        return name();
    }
}

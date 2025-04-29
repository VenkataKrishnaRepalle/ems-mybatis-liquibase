package com.learning.emsmybatisliquibase.utils;

import com.learning.emsmybatisliquibase.dto.pagination.RequestQuery;
import org.apache.commons.lang3.StringUtils;

public class UtilityService {

    private static final String YEAR = "year";

    public static Integer getYear(RequestQuery requestQuery) {
        if (StringUtils.isNotBlank(requestQuery.getPropertyValue(YEAR))) {
            return Integer.parseInt(requestQuery.getPropertyValue(YEAR));
        }
        return null;
    }
}

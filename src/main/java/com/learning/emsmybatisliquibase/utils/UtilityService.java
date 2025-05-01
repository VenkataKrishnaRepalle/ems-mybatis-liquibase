package com.learning.emsmybatisliquibase.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.emsmybatisliquibase.dto.pagination.RequestQuery;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UtilityService {

    private static final String YEAR = "year";

    private static final String LOCATION = "location";

    private static final String LATITUDE = "latitude";

    private static final String LONGITUDE = "longitude";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Integer getYear(RequestQuery requestQuery) {
        if (StringUtils.isNotBlank((String) requestQuery.getPropertyValue(YEAR))) {
            return Integer.parseInt((String) requestQuery.getPropertyValue(YEAR));
        }
        return null;
    }

    public static Map<String, String> getLocationInfo(RequestQuery requestQuery) throws JsonProcessingException {
        Object location = requestQuery.getPropertyValue(LOCATION);
        if (location instanceof Map<?, ?>) {
            Map<String, String> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) location).entrySet()) {
                result.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }

            return result;
        }

        return Collections.emptyMap();
    }

    public static String getPlatform(RequestQuery requestQuery) {
        Object platform = requestQuery.getPropertyValue("platform");
        if (platform instanceof String) {
            return (String) platform;
        }
        return null;
    }

}

package com.learning.emsmybatisliquibase.dto.pagination;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestQuery {
    private Map<String, Object> properties = new ConcurrentHashMap<>();

    @JsonAnySetter
    public void setProperties(String key, Object value) {
        properties.put(key, value);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public String getPropertyValue(String propertyName) {
        return (String) properties.get(propertyName);
    }
}

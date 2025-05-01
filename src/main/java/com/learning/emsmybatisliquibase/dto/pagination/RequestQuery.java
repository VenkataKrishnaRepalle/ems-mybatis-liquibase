package com.learning.emsmybatisliquibase.dto.pagination;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RequestQuery {
    private final Map<String, Object> properties = new ConcurrentHashMap<>();

    @JsonAnySetter
    public void setProperties(String key, Object value) {
        properties.put(key, value);
    }

    public Object getPropertyValue(String propertyName) {
        return (Object) properties.get(propertyName);
    }
}

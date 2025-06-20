package com.learning.emsmybatisliquibase.dto.pagination;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@ToString
public class RequestQuery {
    private final Map<String, Object> properties = new ConcurrentHashMap<>();

    @JsonAnySetter
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public void removeProperty(String propertyName) {
        properties.remove(propertyName);
    }

    public Object getPropertyValue(String propertyName) {
        return (Object) properties.get(propertyName);
    }

    public String getPropertyAsString(String propertyName) {
        var value = properties.get(propertyName);
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    public List<?> getPropertyAsList(String propertyName) {
        var lists = properties.get(propertyName);
        if (lists instanceof List<?>) {
            return (List<?>) lists;
        }
        return null;
    }
}

package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.Timeline;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailsDto extends Timeline {

    private UUID uuid;

    private String firstName;

    private String lastName;

    private String email;
}

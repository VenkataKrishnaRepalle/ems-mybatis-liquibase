package com.learning.emsmybatisliquibase.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalaryStructure implements Serializable {
    private UUID uuid;

    private Double salary;

    private Double basicSalary;

    private Double quarterlyBonus;

    private Double monthlyBonus;

    private UUID profileUuid;
}

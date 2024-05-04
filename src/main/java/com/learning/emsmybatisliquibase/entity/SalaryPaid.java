package com.learning.emsmybatisliquibase.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalaryPaid implements Serializable {

    private UUID uuid;

    private SalaryStructure salaryStructure;

    private Double basicAmount;

    private Double quarterlyBonus;

    private Double monthlyBonus;

    private Double totalAmount;

    private Instant createdTime;
}

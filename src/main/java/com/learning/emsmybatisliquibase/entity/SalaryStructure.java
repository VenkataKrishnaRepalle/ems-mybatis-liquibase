package com.learning.emsmybatisliquibase.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.List;
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

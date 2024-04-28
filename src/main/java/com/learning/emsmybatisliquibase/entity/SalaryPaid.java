package com.learning.emsmybatisliquibase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;
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

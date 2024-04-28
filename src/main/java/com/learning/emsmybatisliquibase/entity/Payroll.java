package com.learning.emsmybatisliquibase.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payroll implements Serializable {

    private UUID uuid;

    private UUID employeeUuid;

    private UUID profileUuid;

    private UUID salaryPaidUuid;

    private Date date;

    private Instant totalAmount;
}

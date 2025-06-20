package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.enums.CertificationLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificationRequestDto {

    private UUID employeeUuid;

    private UUID certificationCategoryUuid;

    private String certificationCategoryName;

    private String name;

    private String skillSets;

    private CertificationLevel level;

    private Date certifiedDate;

    private Date expiryDate;
}

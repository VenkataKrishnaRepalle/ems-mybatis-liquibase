package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.enums.ReviewStatus;
import com.learning.emsmybatisliquibase.entity.enums.ReviewType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddReviewRequestDto {

    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID timelineUuid;

    @Enumerated(EnumType.STRING)
    private ReviewType type;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    private String whatWentWell;

    private String whatDoneBetter;

    private String wayForward;

    private String overallComments;
}

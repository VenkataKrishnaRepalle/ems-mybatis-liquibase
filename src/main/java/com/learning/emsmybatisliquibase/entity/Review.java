package com.learning.emsmybatisliquibase.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review implements Serializable {

    private UUID uuid;

    private UUID timelineUuid;

    private ReviewType reviewType;

    private String whatWentWell;

    private String whatDoneBetter;

    private String wayForward;

    private String overallComments;

    private String managerWhatWentWell;

    private String managerWhatDoneBetter;

    private String managerWayForward;

    private String managerOverallComments;

    private ReviewStatus reviewStatus;

    private ReviewRating reviewRating;

    private Instant createdTime;

    private Instant lastUpdatedTime;
}

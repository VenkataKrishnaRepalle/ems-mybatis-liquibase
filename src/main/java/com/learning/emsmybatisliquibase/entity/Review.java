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

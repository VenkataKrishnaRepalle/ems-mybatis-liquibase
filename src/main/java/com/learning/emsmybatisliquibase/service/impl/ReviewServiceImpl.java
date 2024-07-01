package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.ReviewDao;
import com.learning.emsmybatisliquibase.dao.TimelineDao;
import com.learning.emsmybatisliquibase.dto.AddReviewRequestDto;
import com.learning.emsmybatisliquibase.entity.Review;
import com.learning.emsmybatisliquibase.entity.ReviewStatus;
import com.learning.emsmybatisliquibase.entity.Timeline;
import com.learning.emsmybatisliquibase.exception.FoundException;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import com.learning.emsmybatisliquibase.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

import static com.learning.emsmybatisliquibase.exception.errorcodes.ReviewErrorCodes.*;
import static com.learning.emsmybatisliquibase.exception.errorcodes.TimelineErrorCodes.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final TimelineDao timelineDao;

    private final ReviewDao reviewDao;

    private final EmployeeService employeeService;

    @Override
    public Review add(UUID employeeUuid, AddReviewRequestDto employeeReviewDto) {
        var timeline = timelineDao.getByTimelineIdAndReviewType(employeeReviewDto.getTimelineUuid(), employeeReviewDto.getType());

        var reviewTimeline = reviewDao.getByTimelineId(employeeReviewDto.getTimelineUuid());
        if (reviewTimeline != null) {
            throw new FoundException(REVIEW_ALREADY_EXISTS.code(), "Review already exists");
        }

        validateTimeline(employeeUuid, timeline);

        var review = buildReview(employeeReviewDto);

        try {
            if (0 == reviewDao.insert(review)) {
                throw new IntegrityException(REVIEW_NOT_CREATED.code(), "Review is not inserted with Id: " + review.getUuid());
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(REVIEW_NOT_CREATED.code(), exception.getCause().getMessage());
        }

        return review;
    }

    private Review buildReview(AddReviewRequestDto dto) {
        return Review.builder()
                .uuid(UUID.randomUUID())
                .timelineUuid(dto.getTimelineUuid())
                .type(dto.getType())
                .whatWentWell(dto.getWhatWentWell())
                .whatDoneBetter(dto.getWhatDoneBetter())
                .wayForward(dto.getWayForward())
                .overallComments(dto.getOverallComments())
                .status(ReviewStatus.WAITING_FOR_APPROVAL)
                .createdTime(Instant.now())
                .updatedTime(Instant.now())
                .build();
    }

    @Override
    public Review update(UUID employeeUuid, UUID reviewUuid, Review review) {
        var timeline = timelineDao.getByTimelineIdAndReviewType(review.getTimelineUuid(), review.getType());

        var reviewTimeline = reviewDao.getByTimelineId(review.getTimelineUuid());

        if (reviewTimeline == null) {
            throw new NotFoundException(REVIEW_NOT_EXISTS.code(), "Review not found with Id: " + reviewUuid);
        }

        validateTimeline(employeeUuid, timeline);

        updateReview(reviewTimeline, review, employeeUuid);

        try {
            if (0 == reviewDao.update(reviewTimeline)) {
                throw new IntegrityException(REVIEW_NOT_UPDATED.code(), "Review not updated for Id: " + reviewUuid);
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(REVIEW_NOT_UPDATED.code(), exception.getCause().getMessage());
        }
        return reviewTimeline;
    }

    @Override
    public Review getById(UUID reviewUuid) {
        var review = reviewDao.getById(reviewUuid);
        if (review == null) {
            throw new NotFoundException(REVIEW_NOT_EXISTS.code(), "Review not found with Id: " + reviewUuid);
        }
        return review;
    }

    @Override
    public void deleteById(UUID reviewUuid) {
        getById(reviewUuid);
        try {
            if (0 == reviewDao.delete(reviewUuid)) {
                throw new IntegrityException(REVIEW_NOT_DELETED.code(), "Review not deleted with Id: " + reviewUuid);
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(REVIEW_NOT_DELETED.code(), exception.getCause().getMessage());
        }
        var isReviewExists = reviewDao.getById(reviewUuid);
        if (isReviewExists != null) {
            throw new FoundException(REVIEW_NOT_DELETED.code(), "Review not deleted with Id: " + reviewUuid);
        }
    }

    private void updateReview(Review existingReview, Review updatedReview, UUID employeeUuid) {
        var currentUser = getCurrentUser();
        var employee = employeeService.getById(employeeUuid);

        if (currentUser.equals(employeeUuid)) {
            updateEmployeeReview(existingReview, updatedReview);
        } else if (currentUser.equals(employee.getManagerUuid())) {
            updateManagerReview(existingReview, updatedReview);
        }
    }

    private void updateEmployeeReview(Review existingReview, Review updatedReview) {
        existingReview.setWhatWentWell(updatedReview.getWhatWentWell());
        existingReview.setWhatDoneBetter(updatedReview.getWhatDoneBetter());
        existingReview.setWayForward(updatedReview.getWayForward());
        existingReview.setOverallComments(updatedReview.getOverallComments());
        existingReview.setStatus(ReviewStatus.WAITING_FOR_APPROVAL);
        existingReview.setType(updatedReview.getType());
        existingReview.setUpdatedTime(Instant.now());
    }

    private void updateManagerReview(Review existingReview, Review updatedReview) {
        existingReview.setWhatWentWell(updatedReview.getWhatWentWell());
        existingReview.setWhatDoneBetter(updatedReview.getWhatDoneBetter());
        existingReview.setWayForward(updatedReview.getWayForward());
        existingReview.setOverallComments(updatedReview.getOverallComments());
        existingReview.setStatus(updatedReview.getStatus());
        existingReview.setType(updatedReview.getType());
        existingReview.setManagerWhatWentWell(updatedReview.getManagerWhatWentWell());
        existingReview.setManagerWhatDoneBetter(updatedReview.getManagerWhatDoneBetter());
        existingReview.setManagerWayForward(updatedReview.getManagerWayForward());
        existingReview.setManagerOverallComments(updatedReview.getManagerOverallComments());
        existingReview.setRating(updatedReview.getRating());
        existingReview.setUpdatedTime(Instant.now());
    }

    private void validateTimeline(UUID employeeUuid, Timeline timeline) {
        var currentUser = getCurrentUser();
        var employee = employeeService.getById(employeeUuid);

        if (!currentUser.equals(employeeUuid) && !currentUser.equals(employee.getManagerUuid())) {
            throw new IntegrityException("INVALID_USER", "Invalid User trying to insert data");
        }

        switch (timeline.getStatus()) {
            case COMPLETED:
                throw new InvalidInputException(TIMELINE_COMPLETED.code(), "Review already completed");
            case LOCKED:
                throw new InvalidInputException(TIMELINE_LOCKED.code(), "Review already locked");
            case NOT_STARTED:
                throw new InvalidInputException(TIMELINE_NOT_STARTED.code(), "Review not started");
            default:
                break;
        }
    }

    private UUID getCurrentUser() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}

package org.hrd.finalprojectmuseum.service;

import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.request.ReplyRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorReviewRequest;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReview;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReviewStatistics;
import org.hrd.finalprojectmuseum.model.enums.ReviewType;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    UUID getVisitorIdByUserId(UUID userId);
    VisitorReview addVisitorReview(UUID museumId, UUID visitorId, VisitorReviewRequest visitorReviewRequest);
    List<VisitorReview> getAllVisitorReviews(UUID museumId, Integer page, Integer size, ReviewType reviewType);
    VisitorReview updateVisitorReview(UUID reviewId, UUID visitorId, VisitorReviewRequest visitorReviewRequest);
    Integer getAllVisitorReviews(UUID museumId);
    void deleteVisitorReview(UUID reviewId, UUID visitorId);
    VisitorReviewStatistics getVisitorReviewStatistics(UUID museumId);

    void deleteVisitorReviewByMuseumOwner(UUID reviewId, UUID museumId);

    VisitorReview getVisitorReviewByVisitorId(UUID museumId, UUID visitorId);

    VisitorReview addAndUpdateReplyReview(UUID museumId, UUID reviewId, ReplyRequest replyRequest);
}

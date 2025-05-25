package org.hrd.finalprojectmuseum.service.visitor;

import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorReviewRequest;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReview;

import java.util.List;
import java.util.UUID;

public interface VisitorReviewService {
    UUID getVisitorIdByUserId(UUID userId);
    VisitorReview addVisitorReview(UUID userId, UUID visitorId, VisitorReviewRequest visitorReviewRequest);
    List<VisitorReview> getAllVisitorReviews(UUID museumId);
}

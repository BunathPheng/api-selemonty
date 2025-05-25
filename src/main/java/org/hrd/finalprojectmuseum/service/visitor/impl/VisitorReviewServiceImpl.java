package org.hrd.finalprojectmuseum.service.visitor.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorReviewRequest;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReview;
import org.hrd.finalprojectmuseum.repository.visitor.VisitorReviewRepository;
import org.hrd.finalprojectmuseum.service.visitor.VisitorReviewService;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitorReviewServiceImpl implements VisitorReviewService {

    private final VisitorReviewRepository visitorReviewRepository;
    LocalDateTime updatedAt = LocalDateTime.now();

    @Override
    public UUID getVisitorIdByUserId(UUID userId) {
        UUID visitorId = visitorReviewRepository.retrieveVisitorIDbyUserID(userId);
        if (visitorId == null) {
            throw new AppNotFoundException("Visitor ID Not Found");
        }
        return visitorId;
    }

    @Override
    public VisitorReview addVisitorReview(UUID museumId, UUID visitorId, VisitorReviewRequest visitorReviewRequest) {
        if (!visitorReviewRepository.retrieveMuseumId(museumId)){
            throw new AppNotFoundException("Museum ID Not Found");
        }
        return visitorReviewRepository.createVisitorReview(museumId, visitorId, visitorReviewRequest, updatedAt);
    }
}

package org.hrd.finalprojectmuseum.service.visitor.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorReviewRequest;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReview;
import org.hrd.finalprojectmuseum.model.enums.ReviewType;
import org.hrd.finalprojectmuseum.repository.visitor.VisitorReviewRepository;
import org.hrd.finalprojectmuseum.service.visitor.VisitorReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        if (visitorReviewRepository.retrieveVisitorId(visitorId)) {
            throw new AppBadRequestException("Visitor ID Already Exists");
        }
        VisitorReview visitorReview = visitorReviewRepository.createVisitorReview(museumId, visitorId, visitorReviewRequest, updatedAt);
        visitorReview.setIsReviewed(true);
        return visitorReview;
    }

    @Override
    public List<VisitorReview> getAllVisitorReviews(UUID museumId, Integer page, Integer size, ReviewType reviewType) {
        if (!visitorReviewRepository.retrieveMuseumId(museumId)){
            throw new AppNotFoundException("Museum ID Not Found");
        }

        int offset = (page - 1) * size;
        List<VisitorReview> visitorReviews = new ArrayList<>();

        if (reviewType == ReviewType.MOST_RECENTLY){
            visitorReviews = visitorReviewRepository.retrieveAllVisitorReviewsRecently(museumId, size, offset);
            for(VisitorReview visitorReview : visitorReviews){
                visitorReview.setIsReviewed(true);
            }
        }else if (reviewType == ReviewType.HIGHEST_RATE){
            visitorReviews = visitorReviewRepository.retrieveAllVisitorReviewsHighest(museumId, size, offset);
            for(VisitorReview visitorReview : visitorReviews){
                visitorReview.setIsReviewed(true);
            }
        } else if (reviewType == ReviewType.LOWEST_RATE) {
            visitorReviews = visitorReviewRepository.retrieveAllVisitorReviewsLowest(museumId, size, offset);
            for(VisitorReview visitorReview : visitorReviews){
                visitorReview.setIsReviewed(true);
            }
        }
        return visitorReviews;
    }

    @Override
    public VisitorReview updateVisitorReview(UUID reviewId, UUID visitorId, VisitorReviewRequest visitorReviewRequest) {
        if (!visitorReviewRepository.retrieveReviewId(reviewId)){
            throw new AppNotFoundException("Review ID Not Found");
        }
        VisitorReview updateVisitorReview = visitorReviewRepository.updateVisitorReview(reviewId, visitorId, visitorReviewRequest, updatedAt);
        updateVisitorReview.setIsReviewed(true);
        return updateVisitorReview;
    }

    @Override
    public Integer getAllVisitorReviews(UUID museumId) {
        return visitorReviewRepository.countAllVisitorReviews(museumId);
    }

    @Override
    public void deleteVisitorReview(UUID reviewId, UUID visitorId) {
        if (!visitorReviewRepository.retrieveReviewId(reviewId)){
            throw new AppNotFoundException("Review ID Not Found");
        }
        visitorReviewRepository.deleteVisitorReview(reviewId, visitorId);
    }


}

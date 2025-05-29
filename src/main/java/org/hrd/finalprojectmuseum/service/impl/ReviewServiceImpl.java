package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorReviewRequest;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReview;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReviewStatistics;
import org.hrd.finalprojectmuseum.model.enums.ReviewType;
import org.hrd.finalprojectmuseum.repository.ReviewRepository;
import org.hrd.finalprojectmuseum.service.ReviewService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    LocalDateTime updatedAt = LocalDateTime.now();

    @Override
    public UUID getVisitorIdByUserId(UUID userId) {
        UUID visitorId = reviewRepository.retrieveVisitorIDbyUserID(userId);
        if (visitorId == null) {
            throw new AppNotFoundException("Visitor ID Not Found");
        }
        return visitorId;
    }

    @Override
    public VisitorReview addVisitorReview(UUID museumId, UUID visitorId, VisitorReviewRequest visitorReviewRequest) {
        if (!reviewRepository.retrieveMuseumId(museumId)){
            throw new AppNotFoundException("Museum ID Not Found");
        }
        if (reviewRepository.retrieveVisitorId(visitorId)) {
            throw new AppBadRequestException("Visitor ID Already Exists");
        }
        VisitorReview visitorReview = reviewRepository.createVisitorReview(museumId, visitorId, visitorReviewRequest, updatedAt);
        visitorReview.setIsReviewed(true);
        return visitorReview;
    }

    @Override
    public List<VisitorReview> getAllVisitorReviews(UUID museumId, Integer page, Integer size, ReviewType reviewType) {
        if (!reviewRepository.retrieveMuseumId(museumId)){
            throw new AppNotFoundException("Museum ID Not Found");
        }

        int offset = (page - 1) * size;
        List<VisitorReview> visitorReviews = new ArrayList<>();

        if (reviewType == ReviewType.MOST_RECENTLY){
            visitorReviews = reviewRepository.retrieveAllVisitorReviewsRecently(museumId, size, offset);
            for(VisitorReview visitorReview : visitorReviews){
                visitorReview.setIsReviewed(true);
            }
        }else if (reviewType == ReviewType.HIGHEST_RATE){
            visitorReviews = reviewRepository.retrieveAllVisitorReviewsHighest(museumId, size, offset);
            for(VisitorReview visitorReview : visitorReviews){
                visitorReview.setIsReviewed(true);
            }
        } else if (reviewType == ReviewType.LOWEST_RATE) {
            visitorReviews = reviewRepository.retrieveAllVisitorReviewsLowest(museumId, size, offset);
            for(VisitorReview visitorReview : visitorReviews){
                visitorReview.setIsReviewed(true);
            }
        }
        return visitorReviews;
    }

    @Override
    public VisitorReview updateVisitorReview(UUID reviewId, UUID visitorId, VisitorReviewRequest visitorReviewRequest) {
        if (!reviewRepository.retrieveReviewId(reviewId)){
            throw new AppNotFoundException("Review ID Not Found");
        }
        VisitorReview updateVisitorReview = reviewRepository.updateVisitorReview(reviewId, visitorId, visitorReviewRequest, updatedAt);
        updateVisitorReview.setIsReviewed(true);
        return updateVisitorReview;
    }

    @Override
    public Integer getAllVisitorReviews(UUID museumId) {
        return reviewRepository.countAllVisitorReviews(museumId);
    }

    @Override
    public void deleteVisitorReview(UUID reviewId, UUID visitorId) {
        if (!reviewRepository.retrieveReviewId(reviewId)){
            throw new AppNotFoundException("Review ID Not Found");
        }
        reviewRepository.deleteVisitorReview(reviewId, visitorId);
    }

    @Override
    public VisitorReviewStatistics getVisitorReviewStatistics(UUID museumId) {
        if (!reviewRepository.retrieveMuseumId(museumId)){
            throw new AppNotFoundException("Museum ID Not Found");
        }

        VisitorReviewStatistics statistics = reviewRepository.retriveVisitorReviewStatistics(museumId);

        if (statistics == null || statistics.getTotalReviews() == 0) {
            statistics = new VisitorReviewStatistics();
            statistics.setAverageRating(BigDecimal.ZERO);
            statistics.setTotalReviews(0);
            statistics.setFiveStars(0);
            statistics.setFourStars(0);
            statistics.setThreeStars(0);
            statistics.setTwoStars(0);
            statistics.setOneStar(0);
        }

        return statistics;
    }


}

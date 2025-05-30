package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorReviewRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReview;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReviewStatistics;
import org.hrd.finalprojectmuseum.model.enums.ReviewType;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.service.AppUserService;
import org.hrd.finalprojectmuseum.service.MuseumService;
import org.hrd.finalprojectmuseum.service.ProfileService;
import org.hrd.finalprojectmuseum.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewsController {
    private final ReviewService reviewService;
    private final AppUserService appUserService;
    private final ProfileService profileService;
    private final MuseumService museumService;

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @PostMapping("/{museum-id}")
    @Operation(summary = "Create a review for a museum")
    public ResponseEntity<ApiResponse<VisitorReview>> addVisitorReview(
            @PathVariable("museum-id") UUID museumId,
            @Valid @RequestBody VisitorReviewRequest visitorReviewRequest) {

        UUID visitorId = reviewService.getVisitorIdByUserId(appUserService.getUserId());

        VisitorReview newReview = reviewService.addVisitorReview(museumId, visitorId, visitorReviewRequest);

        ApiResponse<VisitorReview> response = ApiResponse.<VisitorReview>builder()
                .success(true)
                .message("Visitor review created successfully")
                .payload(newReview)
                .status(HttpStatus.CREATED)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{museum-id}")
    @Operation(summary = "Get all reviews of a museum")
    public ResponseEntity<ApiResponse<List<VisitorReview>>> getVisitorReview(
            @PathVariable("museum-id") UUID museumId,
            @RequestParam(defaultValue = "1") @Positive @Min(value = 1, message = "must greater than 0") Integer page,
            @RequestParam(defaultValue = "3") @Positive @Min(value = 1, message = "must greater than 0") Integer size,
            @RequestParam("reviewType") ReviewType reviewType){

        List<VisitorReview> reviews = reviewService.getAllVisitorReviews(museumId, page, size, reviewType);

        Integer totalReviews = reviewService.getAllVisitorReviews(museumId);

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalReviews, page, size);

        ApiResponse<List<VisitorReview>> response = ApiResponse.<List<VisitorReview>>builder()
                .success(true)
                .message("Review retrieve successfully")
                .payload(reviews)
                .pagination(pagination)
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @PutMapping("/{review-Id}")
    @Operation(summary = "Update a review of a museum")
    public ResponseEntity<ApiResponse<VisitorReview>> updateVisitorReview(
            @PathVariable("review-Id") UUID reviewId,
            @Valid @RequestBody VisitorReviewRequest visitorReviewRequest) {

        UUID visitorId = reviewService.getVisitorIdByUserId(appUserService.getUserId());

        VisitorReview updateVisitorReview = reviewService.updateVisitorReview(reviewId, visitorId, visitorReviewRequest);

        ApiResponse<VisitorReview> response = ApiResponse.<VisitorReview>builder()
                .success(true)
                .message("Visitor review updated successfully")
                .payload(updateVisitorReview)
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_VISITOR') or hasRole('ROLE_MUSEUM_OWNER')")
    @DeleteMapping("/{review-Id}")
    @Operation(summary = "Delete visitor review for a museum")
    public ResponseEntity<ApiResponse<VisitorReview>> deleteVisitorReviewById(@PathVariable("review-Id") UUID reviewId) {
        AppUserRegister appUser = appUserService.getAppUserRegister();
        if (appUser.getRole() == Role.ROLE_VISITOR){
            reviewService.deleteVisitorReview(reviewId, reviewService.getVisitorIdByUserId(appUser.getUserId()));
        }else if(appUser.getRole() == Role.ROLE_MUSEUM_OWNER){
            MuseumOwner museum = profileService.getMuseumOwnerByUserId(appUser.getUserId());
            reviewService.deleteVisitorReviewByMuseumOwner(reviewId, museum.getMuseumId());
        }
        ApiResponse<VisitorReview> response = ApiResponse.<VisitorReview>builder()
                .success(true)
                .message("Visitor review deleted successfully")
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{museum-id}/statistics")
    @Operation(summary = "Get review statistics for a museum")
    public ResponseEntity<ApiResponse<VisitorReviewStatistics>> getReviewStatistics(
            @PathVariable("museum-id") UUID museumId) {

        VisitorReviewStatistics statistics = reviewService.getVisitorReviewStatistics(museumId);

        ApiResponse<VisitorReviewStatistics> response = ApiResponse.<VisitorReviewStatistics>builder()
                .success(true)
                .message("Review statistics retrieved successfully")
                .payload(statistics)
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }
}

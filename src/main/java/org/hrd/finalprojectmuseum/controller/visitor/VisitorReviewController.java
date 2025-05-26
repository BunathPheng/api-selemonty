package org.hrd.finalprojectmuseum.controller.visitor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorReviewRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReview;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReviewStatistics;
import org.hrd.finalprojectmuseum.model.enums.ReviewType;
import org.hrd.finalprojectmuseum.service.visitor.VisitorReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/visitor/reviews")
@PreAuthorize("hasRole('ROLE_VISITOR')")
@RequiredArgsConstructor
@Tag(name = "visitor-review-controller")
public class VisitorReviewController {
    private final VisitorReviewService visitorReviewService;

    private UUID getVisitorIdByUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());

        return visitorReviewService.getVisitorIdByUserId(userId);
    }

    @PostMapping("/museum/{museum-id}")
    @Operation(summary = "Create a review for a museum")
    public ResponseEntity<ApiResponse<VisitorReview>> addVisitorReview(
            @PathVariable("museum-id") UUID museumId,
            @Valid @RequestBody VisitorReviewRequest visitorReviewRequest) {

        UUID visitorId = getVisitorIdByUserId();

        VisitorReview newReview = visitorReviewService.addVisitorReview(museumId, visitorId, visitorReviewRequest);

        ApiResponse<VisitorReview> response = ApiResponse.<VisitorReview>builder()
                .success(true)
                .message("Visitor review created successfully")
                .payload(newReview)
                .status(HttpStatus.CREATED)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/museum/{museum-id}")
    @Operation(summary = "Get all reviews of a museum")
    public ResponseEntity<ApiResponse<List<VisitorReview>>> getVisitorReview(
            @PathVariable("museum-id") UUID museumId,
            @RequestParam(defaultValue = "1") @Positive @Min(value = 1, message = "must greater than 0") Integer page,
            @RequestParam(defaultValue = "3") @Positive @Min(value = 1, message = "must greater than 0") Integer size,
            @RequestParam("reviewType") ReviewType reviewType){

        List<VisitorReview> reviews = visitorReviewService.getAllVisitorReviews(museumId, page, size, reviewType);

        Integer totalReviews = visitorReviewService.getAllVisitorReviews(museumId);

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

    @PutMapping("/{review-Id}")
    @Operation(summary = "Update a review of a museum")
    public ResponseEntity<ApiResponse<VisitorReview>> updateVisitorReview(
            @PathVariable("review-Id") UUID reviewId,
            @Valid @RequestBody VisitorReviewRequest visitorReviewRequest) {

        UUID visitorId = getVisitorIdByUserId();

        VisitorReview updateVisitorReview = visitorReviewService.updateVisitorReview(reviewId, visitorId, visitorReviewRequest);

        ApiResponse<VisitorReview> response = ApiResponse.<VisitorReview>builder()
                .success(true)
                .message("Visitor review updated successfully")
                .payload(updateVisitorReview)
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{review-Id}")
    @Operation(summary = "Delete visitor review for a museum")
    public ResponseEntity<ApiResponse<VisitorReview>> deleteVisitorReviewById(@PathVariable("review-Id") UUID reviewId) {
        UUID visitorId = getVisitorIdByUserId();
        visitorReviewService.deleteVisitorReview(reviewId, visitorId);

        ApiResponse<VisitorReview> response = ApiResponse.<VisitorReview>builder()
                .success(true)
                .message("Visitor review deleted successfully")
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/museum/{museum-id}/statistics")
    @Operation(summary = "Get review statistics for a museum")
    public ResponseEntity<ApiResponse<VisitorReviewStatistics>> getReviewStatistics(
            @PathVariable("museum-id") UUID museumId) {

        VisitorReviewStatistics statistics = visitorReviewService.getVisitorReviewStatistics(museumId);

        ApiResponse<VisitorReviewStatistics> response = ApiResponse.<VisitorReviewStatistics>builder()
                .success(true)
                .message("Review statistics retrieved successfully")
                .payload(statistics)
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }
}

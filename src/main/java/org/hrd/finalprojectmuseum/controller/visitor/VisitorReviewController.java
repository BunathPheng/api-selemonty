package org.hrd.finalprojectmuseum.controller.visitor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorReviewRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReview;
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

    private UUID getMuseumIdByUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());

        return visitorReviewService.getVisitorIdByUserId(userId);
    }

    @PostMapping("/museum/{museumId}")
    @Operation(summary = "Create a review for a museum")
    public ResponseEntity<ApiResponse<VisitorReview>> addVisitorReview(
            @PathVariable UUID museumId,
            @Valid @RequestBody VisitorReviewRequest visitorReviewRequest) {

        UUID visitorId = getMuseumIdByUserId();

        VisitorReview review = visitorReviewService.addVisitorReview(museumId, visitorId, visitorReviewRequest);

        ApiResponse<VisitorReview> response = ApiResponse.<VisitorReview>builder()
                .success(true)
                .message("Visitor review created successfully")
                .payload(review)
                .status(HttpStatus.CREATED)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/museum/{museumId}")
    @Operation(summary = "Get all reviews of a museum")
    public ResponseEntity<ApiResponse<List<VisitorReview>>> getVisitorReview(@PathVariable UUID museumId){
        List<VisitorReview> reviews = visitorReviewService.getAllVisitorReviews(museumId);

        ApiResponse<List<VisitorReview>> response = ApiResponse.<List<VisitorReview>>builder()
                .success(true)
                .message("Review created successfully")
                .payload(reviews)
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

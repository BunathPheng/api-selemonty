package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorFavorite;
import org.hrd.finalprojectmuseum.model.enums.FavoriteType;
import org.hrd.finalprojectmuseum.service.FavoriteService;
import org.hrd.finalprojectmuseum.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/favorite")
@PreAuthorize("hasRole('ROLE_VISITOR')")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final ReviewService reviewService;

    private UUID getVisitorIdByUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());

        return reviewService.getVisitorIdByUserId(userId);
    }

    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @PostMapping("/{museum-id}")
    @Operation(summary = "Visitor add museum as their favorite")
    public ResponseEntity<ApiResponse<VisitorFavorite>> createVisitorFavorite(
            @PathVariable("museum-id") UUID museumId,
            @RequestParam("favoriteType") FavoriteType favoriteType) {

        UUID visitorId = getVisitorIdByUserId();

        String message = favoriteType == FavoriteType.FAVORITE ?
                "Museum added to favorites successfully" :
                "Museum removed from favorites successfully";

        favoriteService.addVisitorFavorite(museumId, visitorId, favoriteType);
        ApiResponse<VisitorFavorite> response = ApiResponse.<VisitorFavorite>builder()
                .success(true)
                .message(message)
                .status(HttpStatus.CREATED)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @GetMapping("/{museum-id}")
    @Operation(summary = "Visitor add museum as their favorite")
    public ResponseEntity<ApiResponse<VisitorFavorite>> getVisitorFavorite(@PathVariable("museum-id") UUID museumId) {
        UUID visitorId = getVisitorIdByUserId();

        VisitorFavorite visitorFavorite = favoriteService.getVisitorFavorite(museumId, visitorId);
        ApiResponse<VisitorFavorite> response = ApiResponse.<VisitorFavorite>builder()
                .success(true)
                .message("Visitor favorite fetched successfully")
                .payload(visitorFavorite)
                .status(HttpStatus.CREATED)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.GuideRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Guide;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.enums.GuideStatusType;
import org.hrd.finalprojectmuseum.service.GuideService;
import org.hrd.finalprojectmuseum.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/guide")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class GuidesController {

    private final GuideService guideService;
    private final ProfileService profileService;

    @Operation(summary = "Get all guide. For museum owner")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @GetMapping()
    public ResponseEntity<ApiResponse<ListResponse<Guide>>> getAllTourGuideByMuseumId(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = profileService.getMuseumOwnerByUserId(userId);
        ListResponse<Guide> guideListResponse = guideService.getAllTourGuideByMuseumId(museum.getMuseumId(), null, page, size, null);
        ApiResponse<ListResponse<Guide>> response = ApiResponse.<ListResponse<Guide>>builder()
                .success(true)
                .message("Guides has been successfully retrieved")
                .payload(guideListResponse)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get all guide with filter. For museum owner")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<ListResponse<Guide>>> getAllTourGuideByMuseumIdWithFilter(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size,
            @NotNull(message = "Status Type is required") GuideStatusType statusType
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = profileService.getMuseumOwnerByUserId(userId);
        ListResponse<Guide> guideListResponse = guideService.getAllTourGuideByMuseumId(museum.getMuseumId(), search, page, size, statusType);
        ApiResponse<ListResponse<Guide>> response = ApiResponse.<ListResponse<Guide>>builder()
                .success(true)
                .message("Guides has been successfully retrieved")
                .payload(guideListResponse)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "add new guide. For museum owner")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @PostMapping
    public ResponseEntity<ApiResponse<Guide>> addNewGuide(@RequestBody @Valid GuideRequest guideRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = profileService.getMuseumOwnerByUserId(userId);
        Guide guide = guideService.addNewGuideByMuseumId(museum.getMuseumId(), guideRequest);
        ApiResponse<Guide> response = ApiResponse.<Guide>builder()
                .success(true)
                .message("Guides has been successfully retrieved")
                .payload(guide)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Update guide. For museum owner")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @PutMapping("/{guide-id}")
    public ResponseEntity<ApiResponse<Guide>> updateGuideByGuideId(@PathVariable("guide-id") @NotNull UUID guideId, @RequestBody @Valid GuideRequest guideRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = profileService.getMuseumOwnerByUserId(userId);
        Guide guide = guideService.updateGuideByGuideId(museum.getMuseumId(), guideId, guideRequest);
        ApiResponse<Guide> response = ApiResponse.<Guide>builder()
                .success(true)
                .message("Guides has been successfully updated")
                .payload(guide)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

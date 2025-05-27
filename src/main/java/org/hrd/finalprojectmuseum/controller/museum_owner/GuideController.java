package org.hrd.finalprojectmuseum.controller.museum_owner;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
import org.hrd.finalprojectmuseum.service.MuseumOwnerService;
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
@PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
@SecurityRequirement(name = "bearerAuth")
public class GuideController {

    private final GuideService guideService;
    private final MuseumOwnerService museumOwnerService;

    @GetMapping()
    public ResponseEntity<ApiResponse<ListResponse<Guide>>> getAllTourGuideByMuseumId(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size,
            @NotNull(message = "Status Type is required") GuideStatusType statusType
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = museumOwnerService.getMuseumOwnerByUserId(userId);
        ListResponse<Guide> guideListResponse = guideService.getAllTourGuideByMuseumId(museum.getMuseumId(), search, page, size, statusType);
        ApiResponse<ListResponse<Guide>> response = ApiResponse.<ListResponse<Guide>>builder()
                .success(true)
                .message("Guides has been successfully retrieved")
                .payload(guideListResponse)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Guide>> addNewGuide(@RequestBody @Valid GuideRequest guideRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = museumOwnerService.getMuseumOwnerByUserId(userId);
        Guide guide = guideService.addNewGuideByMuseumId(museum.getMuseumId(), guideRequest);
        ApiResponse<Guide> response = ApiResponse.<Guide>builder()
                .success(true)
                .message("Guides has been successfully retrieved")
                .payload(guide)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{guide-id}")
    public ResponseEntity<ApiResponse<Guide>> updateGuideByGuideId(@PathVariable("guide-id") @NotNull UUID guideId, @RequestBody @Valid GuideRequest guideRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = museumOwnerService.getMuseumOwnerByUserId(userId);
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

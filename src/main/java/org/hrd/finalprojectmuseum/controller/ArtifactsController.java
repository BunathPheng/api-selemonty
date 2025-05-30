package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;
import org.hrd.finalprojectmuseum.service.ArtifactService;
import org.hrd.finalprojectmuseum.service.ZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artifacts")
@RequiredArgsConstructor
public class ArtifactsController {
    private final ArtifactService artifactService;
    private final ZoneService zoneService;

    private UUID getMuseumIdByUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());

        return zoneService.getMuseumIdByUserId(userId);
    }

    @PostMapping("/{zone-id}")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add museum artifact by zone Id")
    public ResponseEntity<ApiResponse<MuseumArtifact>> addMuseumArtifactByZoneId(
            @RequestBody @Valid MuseumArtifactRequest museumArtifactRequest,
            @PathVariable("zone-id") UUID zoneId) {
        UUID museumId = getMuseumIdByUserId();
        MuseumArtifact museumArtifactByZoneId = artifactService.createMuseumArtifactByZoneId(museumArtifactRequest, zoneId, museumId);

        ApiResponse<MuseumArtifact> apiResponse = ApiResponse.<MuseumArtifact>builder()
                .success(true)
                .message("Museum zone details retrieved successfully.")
                .payload(museumArtifactByZoneId)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @PutMapping("/{artifact-id}")
    @Operation(summary = "Update museum artifact by artifact Id")
    public ResponseEntity<ApiResponse<MuseumArtifact>> updateMuseumArtifactByArtifactId(
            @PathVariable("artifact-id") UUID artifactId,
            MuseumArtifactRequest museumArtifactRequest) {

        UUID museumId = getMuseumIdByUserId();
        artifactService.updateMuseumArtifactByArtifactId(artifactId, museumArtifactRequest, museumId);

        ApiResponse<MuseumArtifact> apiResponse = ApiResponse.<MuseumArtifact>builder()
                .success(true)
                .message("Museum artifact updated successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @PatchMapping("/{artifact-id}")
    @Operation(
            summary = "Update museum artifact by artifact Id"
    )
    public ResponseEntity<ApiResponse<MuseumArtifact>> deleteMuseumArtifactByArtifactId(@PathVariable("artifact-id") UUID artifactId){
        UUID museumId = getMuseumIdByUserId();
        artifactService.deleteMuseumArtifactByArtifactId(artifactId, museumId);

        ApiResponse<MuseumArtifact> apiResponse = ApiResponse.<MuseumArtifact>builder()
                .success(true)
                .message("Museum artifact deleted successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{artifact-id}")
    @Operation(
            summary = "Get museum artifact by artifact Id"
    )
    public ResponseEntity<ApiResponse<MuseumArtifact>> getMuseumArtifactByArtifactId(@PathVariable("artifact-id") UUID artifactId){
        MuseumArtifact artifact = artifactService.getMuseumArtifactByArtifactId(artifactId);
        ApiResponse<MuseumArtifact> apiResponse = ApiResponse.<MuseumArtifact>builder()
                .success(true)
                .message("Museum artifact fetched successfully.")
                .payload(artifact)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping()
    @Operation(summary = "Get museum artifact by artifact Id")
    public ResponseEntity<ApiResponse<ListResponse<MuseumArtifact>>> getAllMuseumArtifactByZoneId(
            @RequestParam("zone-id") @NotNull(message = "ZoneID is required") UUID zoneId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") @Positive @Min(value = 1, message = "must greater than 0") Integer page,
            @RequestParam(defaultValue = "3") @Positive @Min(value = 1, message = "must greater than 0") Integer size
    ){
        ListResponse<MuseumArtifact> artifacts = artifactService.getAllMuseumArtifactByZoneId(zoneId, search, page, size);
        ApiResponse<ListResponse<MuseumArtifact>> apiResponse = ApiResponse.<ListResponse<MuseumArtifact>>builder()
                .success(true)
                .message("Museum artifacts fetched successfully.")
                .payload(artifacts)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}

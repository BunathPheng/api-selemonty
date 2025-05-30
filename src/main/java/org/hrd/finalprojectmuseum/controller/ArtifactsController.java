package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;
import org.hrd.finalprojectmuseum.service.ArtifactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/artifacts")
@RequiredArgsConstructor
public class ArtifactsController {
    private final ArtifactService artifactService;

    @PostMapping("/{zone-id}")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @Operation(summary = "Add museum artifact by zone Id")
    public ResponseEntity<ApiResponse<MuseumArtifact>> addMuseumArtifactByZoneId(
            @RequestBody @Valid MuseumArtifactRequest museumArtifactRequest,
            @PathVariable("zone-id") UUID zoneId) {

        MuseumArtifact museumArtifactByZoneId = artifactService.createMuseumArtifactByZoneId(museumArtifactRequest, zoneId);

        ApiResponse<MuseumArtifact> apiResponse = ApiResponse.<MuseumArtifact>builder()
                .success(true)
                .message("Museum zone details retrieved successfully.")
                .payload(museumArtifactByZoneId)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @PutMapping("/{artifact-id}")
    @Operation(summary = "Update museum artifact by artifact Id")
    public ResponseEntity<ApiResponse<MuseumArtifact>> updateMuseumArtifactByArtifactId(
            @PathVariable("artifact-id") UUID artifactId,
            MuseumArtifactRequest museumArtifactRequest) {

        artifactService.updateMuseumArtifactByArtifactId(artifactId, museumArtifactRequest);

        ApiResponse<MuseumArtifact> apiResponse = ApiResponse.<MuseumArtifact>builder()
                .success(true)
                .message("Museum artifact updated successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @PatchMapping("/{artifact-id}")
    @Operation(summary = "Update museum artifact by artifact Id")
    public ResponseEntity<ApiResponse<MuseumArtifact>> deleteMuseumArtifactByArtifactId(@PathVariable("artifact-id") UUID artifactId){
        artifactService.deleteMuseumArtifactByArtifactId(artifactId);

        ApiResponse<MuseumArtifact> apiResponse = ApiResponse.<MuseumArtifact>builder()
                .success(true)
                .message("Museum artifact deleted successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{artifact-id}")
    @Operation(summary = "Get museum artifact by artifact Id")
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
}

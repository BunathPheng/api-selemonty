package org.hrd.finalprojectmuseum.controller.museum_owner;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneUpdateRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;
import org.hrd.finalprojectmuseum.service.museum.MuseumZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/museum-owner/zone")
@PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
@RequiredArgsConstructor
public class MuseumZoneController {

    private final MuseumZoneService museumZoneService;

    @GetMapping("/category")
    @Operation(summary = "Get all museum zone categories")
    public ResponseEntity<ApiResponse<List<MuseumZoneCategory>>> getAllZoneCategories() {
        List<MuseumZoneCategory> museumZoneCategory = museumZoneService.getAllZonesCategories();
        ApiResponse<List<MuseumZoneCategory>> apiResponse = ApiResponse.<List<MuseumZoneCategory>>builder()
                .success(true)
                .message("All Zone categories have been successfully fetched.")
                .payload(museumZoneCategory)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping
    @Operation(summary = "Create museum zone")
    public ResponseEntity<ApiResponse<MuseumZone>> addMuseumZone(@RequestBody @Valid MuseumZoneRequest museumZoneRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());

        UUID museumId = museumZoneService.getMuseumIdByUserId(userId);

        museumZoneService.createMuseumZone(museumZoneRequest, museumId);

        ApiResponse<MuseumZone> apiResponse = ApiResponse.<MuseumZone>builder()
                .success(true)
                .message("Museum zone has been created successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping
    @Operation(summary = "Get all museum zone categories by museum Id")
    public ResponseEntity<ApiResponse<List<MuseumZoneCategory>>> getAllZoneCategoriesByMuseumId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());

        UUID museumId = museumZoneService.getMuseumIdByUserId(userId);

        List<MuseumZoneCategory> museumZoneCategory = museumZoneService.getAllZonesCategoriesByMuseumId(museumId);
        ApiResponse<List<MuseumZoneCategory>> apiResponse = ApiResponse.<List<MuseumZoneCategory>>builder()
                .success(true)
                .message("All Zone categories belong to museum has been successfully fetched.")
                .payload(museumZoneCategory)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{zone-id}")
    @Operation(summary = "Get museum zone detail by museum zone Id")
    public ResponseEntity<ApiResponse<MuseumZone>> getMuseumZoneDetailByZoneId(@PathVariable("zone-id") UUID zoneId) {

        MuseumZone museumZoneDetail = museumZoneService.getMuseumZoneDetailByZoneId(zoneId);

        ApiResponse<MuseumZone> apiResponse = ApiResponse.<MuseumZone>builder()
                .success(true)
                .message("Museum zone details retrieved successfully.")
                .payload(museumZoneDetail)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/{zone-id}")
    @Operation(summary = "Add museum artifact by zone Id")
    public ResponseEntity<ApiResponse<MuseumArtifact>> addMuseumArtifactByZoneId(
            @RequestBody @Valid MuseumArtifactRequest museumArtifactRequest,
            @PathVariable("zone-id") UUID zoneId) {

        MuseumArtifact museumArtifactByZoneId = museumZoneService.createMuseumArtifactByZoneId(museumArtifactRequest, zoneId);

        ApiResponse<MuseumArtifact> apiResponse = ApiResponse.<MuseumArtifact>builder()
                .success(true)
                .message("Museum zone details retrieved successfully.")
                .payload(museumArtifactByZoneId)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping("/{zone-id}")
    @Operation(summary = "Update museum zone detail by zone Id")
    public ResponseEntity<ApiResponse<MuseumZone>> updateMuseumZoneDetailByZoneId(
            @PathVariable("zone-id") UUID zoneId,
            @RequestBody @Valid MuseumZoneUpdateRequest  museumZoneUpdateRequest) {

        museumZoneService.updateMuseumZoneDetailByZoneId(zoneId, museumZoneUpdateRequest);

        ApiResponse<MuseumZone> apiResponse = ApiResponse.<MuseumZone>builder()
                .success(true)
                .message("Museum zone details updated successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping("/artifact/{artifact-id}")
    @Operation(summary = "Update museum artifact by artifact Id")
    public ResponseEntity<ApiResponse<MuseumArtifact>> updateMuseumArtifactByArtifactId(
            @PathVariable("artifact-id") UUID artifactId,
            MuseumArtifactRequest museumArtifactRequest) {

        museumZoneService.updateMuseumArtifactByArtifactId(artifactId, museumArtifactRequest);

        ApiResponse<MuseumArtifact> apiResponse = ApiResponse.<MuseumArtifact>builder()
                .success(true)
                .message("Museum artifact updated successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/artifact/{artifact-id}")
    @Operation(summary = "Update museum artifact by artifact Id")
    public ResponseEntity<ApiResponse<MuseumArtifact>> deleteMuseumArtifactByArtifactId(@PathVariable("artifact-id") UUID artifactId){
        museumZoneService.deleteMuseumArtifactByArtifactId(artifactId);

        ApiResponse<MuseumArtifact> apiResponse = ApiResponse.<MuseumArtifact>builder()
                .success(true)
                .message("Museum artifact deleted successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{zone-id}")
    @Operation(summary = "Delete museum zone zone Id")
    public ResponseEntity<ApiResponse<MuseumZone>> deleteMuseumZoneByZoneId(@PathVariable("zone-id") UUID zoneId) {

        museumZoneService.deleteMuseumZoneByZoneId(zoneId);

        ApiResponse<MuseumZone> apiResponse = ApiResponse.<MuseumZone>builder()
                .success(true)
                .message("Museum zone deleted successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    public ResponseEntity<ApiResponse<>>
}

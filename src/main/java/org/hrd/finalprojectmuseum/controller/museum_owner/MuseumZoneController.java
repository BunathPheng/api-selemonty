package org.hrd.finalprojectmuseum.controller.museum_owner;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;
import org.hrd.finalprojectmuseum.service.museum.MuseumZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/museum-owner/zone")
@RequiredArgsConstructor
public class MuseumZoneController {

    private final MuseumZoneService museumZoneService;

    @GetMapping("/category")
    @Operation(summary = "Get all zone categories")
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
    @Operation(summary = "Get all zone categories by Museum Id")
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

//    @PostMapping("/zone-id")
//    public ResponseEntity<ApiResponse<MuseumArtifact>> addMuseumArtifactByZoneId(@RequestBody @Valid MuseumArtifactRequest museumArtifactRequest) {
//
//        return null;
//    }

}

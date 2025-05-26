package org.hrd.finalprojectmuseum.controller.museum_owner;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneUpdateRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumZoneResponse;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
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
@Tag(name = "museum-owner-zone-controller")
public class MuseumZoneController {

    private final MuseumZoneService museumZoneService;

    private UUID getMuseumIdByUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());

        return museumZoneService.getMuseumIdByUserId(userId);
    }

    @GetMapping("/all-zone-category")
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

        UUID museumId = getMuseumIdByUserId();

        museumZoneService.createMuseumZone(museumZoneRequest, museumId);

        ApiResponse<MuseumZone> apiResponse = ApiResponse.<MuseumZone>builder()
                .success(true)
                .message("Museum zone has been created successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/zone-category")
    @Operation(summary = "Get all museum zone categories belong to museum")
    public ResponseEntity<ApiResponse<List<MuseumZoneCategory>>> getAllZoneCategoriesByMuseumId() {

        UUID museumId = getMuseumIdByUserId();

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

    @GetMapping
    @Operation(summary = "Get all museum zone")
    public ResponseEntity<ApiResponse<List<MuseumZoneResponse>>> getAllMuseumZonesByMuseumId(
            @RequestParam(defaultValue = "1") @Positive @Min(value = 1, message = "must greater than 0") Integer page,
            @RequestParam(defaultValue = "3") @Positive @Min(value = 1, message = "must greater than 0") Integer size) {

        UUID museumId = getMuseumIdByUserId();

        List<MuseumZoneResponse> allMuseumZonesByMuseumId = museumZoneService.getAllMuseumZonesByMuseumId(museumId, size, page);

        Integer totalItems = museumZoneService.getTotalMuseumZonesByMuseumId(museumId);

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);

        ApiResponse<List<MuseumZoneResponse>> apiResponse = ApiResponse.<List<MuseumZoneResponse>>builder()
                .success(true)
                .message("All Museum zones fetched successfully.")
                .payload(allMuseumZonesByMuseumId)
                .pagination(pagination)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())

                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}

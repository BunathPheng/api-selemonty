package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneUpdateRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumZoneResponse;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;
import org.hrd.finalprojectmuseum.service.ZoneService;
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
@RequestMapping("/api/v1/zones")
@RequiredArgsConstructor
public class ZonesController {

    private final ZoneService zoneService;

    private UUID getMuseumIdByUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());

        return zoneService.getMuseumIdByUserId(userId);
    }

    @GetMapping("/zone-category")
    @Operation(summary = "Get all museum zone categories")
    public ResponseEntity<ApiResponse<List<MuseumZoneCategory>>> getAllZoneCategories() {
        List<MuseumZoneCategory> museumZoneCategory = zoneService.getAllZonesCategories();
        ApiResponse<List<MuseumZoneCategory>> apiResponse = ApiResponse.<List<MuseumZoneCategory>>builder()
                .success(true)
                .message("All Zone categories have been successfully fetched.")
                .payload(museumZoneCategory)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @PostMapping
    @Operation(summary = "Create museum zone")
    public ResponseEntity<ApiResponse<MuseumZone>> addMuseumZone(@RequestBody @Valid MuseumZoneRequest museumZoneRequest) {

        UUID museumId = getMuseumIdByUserId();

        zoneService.createMuseumZone(museumZoneRequest, museumId);

        ApiResponse<MuseumZone> apiResponse = ApiResponse.<MuseumZone>builder()
                .success(true)
                .message("Museum zone has been created successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{zone-id}")
    @Operation(summary = "Get museum zone detail by museum zone Id")
    public ResponseEntity<ApiResponse<MuseumZone>> getMuseumZoneDetailByZoneId(@PathVariable("zone-id") UUID zoneId) {

        MuseumZone museumZoneDetail = zoneService.getMuseumZoneDetailByZoneId(zoneId);

        ApiResponse<MuseumZone> apiResponse = ApiResponse.<MuseumZone>builder()
                .success(true)
                .message("Museum zone details retrieved successfully.")
                .payload(museumZoneDetail)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @PutMapping("/{zone-id}")
    @Operation(summary = "Update museum zone detail by zone Id")
    public ResponseEntity<ApiResponse<MuseumZone>> updateMuseumZoneDetailByZoneId(
            @PathVariable("zone-id") UUID zoneId,
            @RequestBody @Valid MuseumZoneUpdateRequest  museumZoneUpdateRequest) {

        UUID museumId = getMuseumIdByUserId();
        zoneService.updateMuseumZoneDetailByZoneId(zoneId, museumZoneUpdateRequest, museumId);

        ApiResponse<MuseumZone> apiResponse = ApiResponse.<MuseumZone>builder()
                .success(true)
                .message("Museum zone details updated successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @PatchMapping("/{zone-id}")
    @Operation(summary = "Delete museum zone zone Id")
    public ResponseEntity<ApiResponse<MuseumZone>> deleteMuseumZoneByZoneId(@PathVariable("zone-id") UUID zoneId) {

        UUID museumId = getMuseumIdByUserId();
        zoneService.deleteMuseumZoneByZoneId(zoneId, museumId);

        ApiResponse<MuseumZone> apiResponse = ApiResponse.<MuseumZone>builder()
                .success(true)
                .message("Museum zone deleted successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping()
    @Operation(summary = "Get all museum zone")
    public ResponseEntity<ApiResponse<ListResponse<MuseumZoneResponse>>> getAllMuseumZonesByMuseumId(
            @RequestParam @NotNull UUID museumId,
            @RequestParam(defaultValue = "1") @Positive @Min(value = 1, message = "must greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Positive @Min(value = 1, message = "must greater than 0") Integer size) {

        List<MuseumZoneResponse> allMuseumZonesByMuseumId = zoneService.getAllMuseumZonesByMuseumId(museumId, null, null, page, size);

        Integer totalItems = zoneService.getTotalMuseumZonesByMuseumId(museumId, null, null);

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);
        ListResponse<MuseumZoneResponse> listResponse = ListResponse.<MuseumZoneResponse>builder()
                .items(allMuseumZonesByMuseumId)
                .pagination(pagination)
                .build();

        ApiResponse<ListResponse<MuseumZoneResponse>> apiResponse = ApiResponse.<ListResponse<MuseumZoneResponse>>builder()
                .success(true)
                .message("All Museum zones fetched successfully.")
                .payload(listResponse)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/filter")
    @Operation(summary = "Get all museum zone")
    public ResponseEntity<ApiResponse<ListResponse<MuseumZoneResponse>>> getAllMuseumZonesByMuseumIdWithFilter(
            @RequestParam @NotNull UUID museumId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "1") @Positive @Min(value = 1, message = "must greater than 0") Integer page,
            @RequestParam(defaultValue = "3") @Positive @Min(value = 1, message = "must greater than 0") Integer size) {

        List<MuseumZoneResponse> allMuseumZonesByMuseumId = zoneService.getAllMuseumZonesByMuseumId(museumId, search, categoryId, page, size);

        Integer totalItems = zoneService.getTotalMuseumZonesByMuseumId(museumId, search, categoryId);

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);
        ListResponse<MuseumZoneResponse> listResponse = ListResponse.<MuseumZoneResponse>builder()
                .pagination(pagination)
                .items(allMuseumZonesByMuseumId)
                .build();

        ApiResponse<ListResponse<MuseumZoneResponse>> apiResponse = ApiResponse.<ListResponse<MuseumZoneResponse>>builder()
                .success(true)
                .message("All Museum zones fetched successfully.")
                .payload(listResponse)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}

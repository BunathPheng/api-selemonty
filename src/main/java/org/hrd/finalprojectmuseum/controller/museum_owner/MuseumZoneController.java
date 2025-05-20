package org.hrd.finalprojectmuseum.controller.museum_owner;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;
import org.hrd.finalprojectmuseum.service.museum.MuseumZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/museum")
@RequiredArgsConstructor
public class MuseumZoneController {

    private final MuseumZoneService museumZoneService;

    @GetMapping("/category")
    @Operation(summary = "Get all zone categories")
    public ResponseEntity<ApiResponse<List<MuseumZoneCategory>>> getAllZoneCategories() {
        List<MuseumZoneCategory> attendees = museumZoneService.getAllZonesCategories();
        ApiResponse<List<MuseumZoneCategory>> apiResponse = ApiResponse.<List<MuseumZoneCategory>>builder()
                .success(true)
                .message("All attendees have been successfully fetched.")
                .payload(attendees)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/museum-id")
    @Operation(summary = "Get all zone categories")
    public ResponseEntity<ApiResponse<MuseumZone>> createMuseumZone(@RequestBody MuseumZoneRequest museumZoneRequest) {
        return null;
    }

}

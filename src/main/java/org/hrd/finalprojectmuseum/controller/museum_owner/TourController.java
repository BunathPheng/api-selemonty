package org.hrd.finalprojectmuseum.controller.museum_owner;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.TourVisitorResponse;
import org.hrd.finalprojectmuseum.model.entity.Guide;
import org.hrd.finalprojectmuseum.model.entity.Tour;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.enums.GuideStatusType;
import org.hrd.finalprojectmuseum.model.enums.TourStatus;
import org.hrd.finalprojectmuseum.repository.TourRepository;
import org.hrd.finalprojectmuseum.service.MuseumOwnerService;
import org.hrd.finalprojectmuseum.service.TourService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/tour")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
@SecurityRequirement(name = "bearerAuth")
public class TourController {

    private final MuseumOwnerService museumOwnerService;
    private final TourService tourService;

    @GetMapping()
    public ResponseEntity<ApiResponse<ListResponse<TourVisitorResponse>>> getAllTourByMuseumId(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size,
            @NotNull(message = "Status Type is required") TourStatus statusType
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = museumOwnerService.getMuseumOwnerByUserId(userId);
        ListResponse<TourVisitorResponse> guideListResponse = tourService.getAllTourByMuseumId(museum.getMuseumId(), search, page, size, statusType);
        ApiResponse<ListResponse<TourVisitorResponse>> response = ApiResponse.<ListResponse<TourVisitorResponse>>builder()
                .success(true)
                .message("Tours has been successfully retrieved")
                .payload(guideListResponse)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{tour-id}")
    public ResponseEntity<ApiResponse<Tour>> getTourByTourId(@PathVariable("tour-id") @NotNull(message = "TourID can't be null") UUID tourId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = museumOwnerService.getMuseumOwnerByUserId(userId);
        Tour tour = tourService.getTourByTourId(museum.getMuseumId(), tourId);
        ApiResponse<Tour> response = ApiResponse.<Tour>builder()
                .success(true)
                .message("Tour has been successfully retrieved")
                .payload(tour)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

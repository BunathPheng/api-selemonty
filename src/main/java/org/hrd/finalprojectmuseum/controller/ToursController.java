package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.AcceptTourRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.Tour;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.model.enums.TourStatus;
import org.hrd.finalprojectmuseum.repository.TourRepository;
import org.hrd.finalprojectmuseum.service.AppUserService;
import org.hrd.finalprojectmuseum.service.ProfileService;
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
@SecurityRequirement(name = "bearerAuth")
public class ToursController {

    private final ProfileService profileService;
    private final TourService tourService;
    private final AppUserService appUserService;

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER') or hasRole('ROLE_VISITOR')")
    @Operation(summary = "Use for get all tour. For museum owner and visitor")
    @GetMapping()
    public ResponseEntity<ApiResponse<ListResponse<Tour>>> getAllTourById(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size,
            @NotNull(message = "Status Type is required") TourStatus statusType
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        AppUserRegister appUserRegister = appUserService.findUserByUserId(userId);
        ListResponse<Tour> guideListResponse = null;
        if (appUserRegister.getRole() == Role.ROLE_MUSEUM_OWNER){
            MuseumOwner museum = profileService.getMuseumOwnerByUserId(userId);
            guideListResponse = tourService.getAllTourByMuseumId(museum.getMuseumId(), null, page, size, statusType);
        } else if (appUserRegister.getRole() == Role.ROLE_VISITOR) {
            Visitor visitor = profileService.getProfile(userId);
            guideListResponse = tourService.getAllTourByVisitorId(visitor.getVisitorId(), null, page, size, statusType);
        }

        ApiResponse<ListResponse<Tour>> response = ApiResponse.<ListResponse<Tour>>builder()
                .success(true)
                .message("Tours has been successfully retrieved")
                .payload(guideListResponse)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER') or hasRole('ROLE_VISITOR')")
    @Operation(summary = "Use for get all tour with filter. Allow guest")
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<ListResponse<Tour>>> getAllTourByIdWithFilter(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size,
            @NotNull(message = "Status Type is required") TourStatus statusType
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        AppUserRegister appUserRegister = appUserService.findUserByUserId(userId);
        ListResponse<Tour> guideListResponse = null;
        if (appUserRegister.getRole() == Role.ROLE_MUSEUM_OWNER){
            MuseumOwner museum = profileService.getMuseumOwnerByUserId(userId);
            guideListResponse = tourService.getAllTourByMuseumId(museum.getMuseumId(), search, page, size, statusType);
        } else if (appUserRegister.getRole() == Role.ROLE_VISITOR) {
            Visitor visitor = profileService.getProfile(userId);
            guideListResponse = tourService.getAllTourByVisitorId(visitor.getVisitorId(), search, page, size, statusType);
        }

        ApiResponse<ListResponse<Tour>> response = ApiResponse.<ListResponse<Tour>>builder()
                .success(true)
                .message("Tours has been successfully retrieved")
                .payload(guideListResponse)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER') or hasRole('ROLE_VISITOR')")
    @Operation(summary = "Use for get tour by tour ID. For museum owner and visitor")
    @GetMapping("/{tour-id}")
    public ResponseEntity<ApiResponse<Tour>> getTourByTourId(@PathVariable("tour-id") @NotNull(message = "TourID can't be null") UUID tourId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        AppUserRegister appUserRegister = appUserService.findUserByUserId(userId);
        Tour tour = null;
        if (appUserRegister.getRole() == Role.ROLE_MUSEUM_OWNER){
            MuseumOwner museum = profileService.getMuseumOwnerByUserId(userId);
            tour = tourService.getTourByTourId(museum.getMuseumId(), tourId);
        } else if (appUserRegister.getRole() == Role.ROLE_VISITOR) {
            Visitor visitor = profileService.getProfile(userId);
            tour = tourService.getTourByTourIdWithVisitorId(visitor.getVisitorId(), tourId);
        }
        ApiResponse<Tour> response = ApiResponse.<Tour>builder()
                .success(true)
                .message("Tour has been successfully retrieved")
                .payload(tour)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @Operation(summary = "Use for accept request tour. For museum owner and visitor")
    @PutMapping("/{tour-id}")
    public ResponseEntity<ApiResponse<Tour>> acceptTour(@PathVariable("tour-id") @NotNull(message = "TourID can't be null") UUID tourId, @RequestBody @Valid AcceptTourRequest acceptTourRequest) {
        Tour tour = tourService.acceptTourByTourId(tourId, acceptTourRequest);
        ApiResponse<Tour> response = ApiResponse.<Tour>builder()
                .success(true)
                .message("Tour has been accepted successfully. Wait for visitor payment!")
                .payload(tour)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER') or hasRole('ROLE_VISITOR')")
    @Operation(summary = "Use to update tour status to paid after visitor paid.")
    @PatchMapping("/{tour-id}")
    public ResponseEntity<ApiResponse<Void>> updateTourStatus(@PathVariable("tour-id") @NotNull(message = "TourID can't be null") UUID tourId) {
        tourService.updateTourStatus(tourId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Tour status has been update to PAID successfully!")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

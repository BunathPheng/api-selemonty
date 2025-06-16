package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.*;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.service.AppUserService;
import org.hrd.finalprojectmuseum.service.ProfileService;
import org.hrd.finalprojectmuseum.service.VisitorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/visitors")
@SecurityRequirement(name = "bearerAuth")
public class VisitorsController {

    private final AppUserService appUserService;
    private final VisitorService visitorService;
    private final ProfileService profileService;

    @Operation(summary = "For museum owner Get all visitor booking their museum. For museum owner only")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @GetMapping("/museum")
    public ResponseEntity<ApiResponse<ListResponse<Visitor>>> getAllVisitorsOfMuseum(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size
    ){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        AppUserRegister user = appUserService.findUserByUserId(userId);
        ListResponse<Visitor> visitorListResponse = null;
        if (user.getRole() == Role.ROLE_MUSEUM_OWNER){
            visitorListResponse = visitorService.getVisitorByUserId(userId, null, page, size);
        }

        ApiResponse<ListResponse<Visitor>> response = ApiResponse.<ListResponse<Visitor>>builder()
                .success(true)
                .message("Visitors fetched successfully")
                .status(HttpStatus.OK)
                .payload(visitorListResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "For museum owner Get Top visitor booking their museum. For museum owner only")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @GetMapping("/museum/top-visitor")
    public ResponseEntity<ApiResponse<List<Visitor>>> getAllVisitorsOfMuseum(){
        UUID museumId = profileService.getMuseumIdByUserId();
        List<Visitor> visitorListResponse = visitorService.getTopVisitorByMuseumId(museumId);
        ApiResponse<List<Visitor>> response = ApiResponse.<List<Visitor>>builder()
                .success(true)
                .message("Top Visitors fetched successfully")
                .status(HttpStatus.OK)
                .payload(visitorListResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "For museum owner Get all visitor with filter booking their museum. For museum owner only")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @GetMapping("/museum/filter")
    public ResponseEntity<ApiResponse<ListResponse<Visitor>>> getAllVisitorsOfMuseumWithFilter(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size
    ){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        AppUserRegister user = appUserService.findUserByUserId(userId);
        ListResponse<Visitor> visitorListResponse = null;
        if (user.getRole() == Role.ROLE_MUSEUM_OWNER){
            visitorListResponse = visitorService.getVisitorByUserId(userId, search, page, size);
        }

        ApiResponse<ListResponse<Visitor>> response = ApiResponse.<ListResponse<Visitor>>builder()
                .success(true)
                .message("Visitors fetched successfully")
                .status(HttpStatus.OK)
                .payload(visitorListResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get all visitor. For admin only")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<ListResponse<Visitor>>> getAllVisitors(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size
    ){

        ListResponse<Visitor>  visitorListResponse = visitorService.getAllVisitor(null, page, size);

        ApiResponse<ListResponse<Visitor>> response = ApiResponse.<ListResponse<Visitor>>builder()
                .success(true)
                .message("Visitors fetched successfully")
                .status(HttpStatus.OK)
                .payload(visitorListResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get all visitor with filter. For admin only")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/filter")
    public ResponseEntity<ApiResponse<ListResponse<Visitor>>> getAllVisitorsWithFilter(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size
    ){

        ListResponse<Visitor>  visitorListResponse = visitorService.getAllVisitor(search, page, size);

        ApiResponse<ListResponse<Visitor>> response = ApiResponse.<ListResponse<Visitor>>builder()
                .success(true)
                .message("Visitors fetched successfully")
                .status(HttpStatus.OK)
                .payload(visitorListResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MUSEUM_OWNER')")
    @GetMapping("/{visitor-id}")
    public ResponseEntity<ApiResponse<Visitor>> getVisitorById(@PathVariable("visitor-id") @NotNull UUID visitorId){
        Visitor visitor = visitorService.getVisitorById(visitorId);
        ApiResponse<Visitor> response = ApiResponse.<Visitor>builder()
                .success(true)
                .message("Visitors fetched successfully")
                .status(HttpStatus.OK)
                .payload(visitor)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/booking")
    public ResponseEntity<ApiResponse<ListResponse<VisitorBooking>>> getAllVisitorsWithBooking(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ){
        ListResponse<VisitorBooking> visitors = visitorService.getAllVisitorBooking(null, page, size);
        ApiResponse<ListResponse<VisitorBooking>> response = ApiResponse.<ListResponse<VisitorBooking>>builder()
                .success(true)
                .message("Visitors fetched successfully")
                .status(HttpStatus.OK)
                .payload(visitors)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/booking/filter")
    public ResponseEntity<ApiResponse<ListResponse<VisitorBooking>>> getAllVisitorsWithBookingFilter(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ){
        ListResponse<VisitorBooking> visitors = visitorService.getAllVisitorBooking(search, page, size);
        ApiResponse<ListResponse<VisitorBooking>> response = ApiResponse.<ListResponse<VisitorBooking>>builder()
                .success(true)
                .message("Visitors fetched successfully")
                .status(HttpStatus.OK)
                .payload(visitors)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Admin dashboard visitor")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{visitor-id}/booking")
    public ResponseEntity<ApiResponse<ListResponse<VisitorBookingDetail>>> getVisitorBookingByVisitorId(
            @PathVariable("visitor-id") @NotNull(message = "Visitor ID is required") UUID visitorId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ){
        ListResponse<VisitorBookingDetail> booking = visitorService.getBookingByVisitorId(visitorId, search, page, size);
        ApiResponse<ListResponse<VisitorBookingDetail>> response = ApiResponse.<ListResponse<VisitorBookingDetail>>builder()
                .success(true)
                .message("Booking fetched successfully")
                .status(HttpStatus.OK)
                .payload(booking)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin dashboard visitor")
    @GetMapping("/{visitor-id}/booking/total")
    public ResponseEntity<ApiResponse<VisitorBookingTotal>> getVisitorBookingTotalByVisitorId(
            @PathVariable("visitor-id") @NotNull(message = "Visitor ID is required") UUID visitorId
    ){
        VisitorBookingTotal booking = visitorService.getVisitorBookingTotalByVisitorId(visitorId);
        ApiResponse<VisitorBookingTotal> response = ApiResponse.<VisitorBookingTotal>builder()
                .success(true)
                .message("Visitor Booking Total fetched successfully")
                .status(HttpStatus.OK)
                .payload(booking)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Admin dashboard visitor")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/stat")
    public ResponseEntity<ApiResponse<VisitorStat>> getVisitorBookingTotalByVisitorIdAndBooking(){
        VisitorStat booking = visitorService.getVisitorStatByVisitorId();
        ApiResponse<VisitorStat> response = ApiResponse.<VisitorStat>builder()
                .success(true)
                .message("Visitor Booking Total fetched successfully")
                .status(HttpStatus.OK)
                .payload(booking)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

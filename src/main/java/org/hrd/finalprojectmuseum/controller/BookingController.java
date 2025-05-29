package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.BookingRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.BookingDetail;
import org.hrd.finalprojectmuseum.model.dto.response.BookingManagement;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.enums.BookingType;
import org.hrd.finalprojectmuseum.service.BookingService;
import org.hrd.finalprojectmuseum.service.ProfileService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/booking")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_VISITOR')")
public class BookingController {
    private final BookingService bookingService;
    private final ProfileService profileService;

    @Operation(summary = "For get all booking of a museum with search and pagination. Only museum owner can use.")
    @GetMapping()
    public ResponseEntity<ApiResponse<ListResponse<BookingManagement>>> getBookingHistory(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = profileService.getMuseumOwnerByUserId(userId);

        ListResponse<BookingManagement> bookings = bookingService.getAllBookingByMuseumId(
                museum.getMuseumId(), search, page, size);

        ApiResponse<ListResponse<BookingManagement>> response = ApiResponse.<ListResponse<BookingManagement>>builder()
                .success(true)
                .message("Bookings retrieved successfully")
                .status(HttpStatus.OK)
                .payload(bookings)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "For booking a ticket. Only visitor can use.")
    @PostMapping("/{museum-id}")
    public ResponseEntity<ApiResponse<Booking>> bookingIndividualByMuseumId(
            @PathVariable("museum-id") @Valid UUID museumId,
            @RequestBody @Valid BookingRequest bookingRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        Visitor visitor = profileService.getProfile(userId);
        Booking booking = bookingService.makeABookingByMuseumId(museumId, visitor.getVisitorId(), bookingRequest);
        ApiResponse<Booking> response = ApiResponse.<Booking>builder()
                .success(true)
                .message("Booking successfully")
                .status(HttpStatus.CREATED)
                .payload(booking)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "For get all booking history of a visitor with search, category and between of two date. Only visitor can use.",
            description = "For Date must follow format (YYYY-MM-DD)"
    )
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Booking>>> getBookingHistory(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "bookingType", required = false) BookingType bookingType,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        Visitor visitor = profileService.getProfile(userId);

        List<Booking> bookings = bookingService.getBookingHistoryByVisitorId(
                visitor.getVisitorId(), search, bookingType, startDate, endDate);

        ApiResponse<List<Booking>> response = ApiResponse.<List<Booking>>builder()
                .success(true)
                .message("Bookings retrieved successfully")
                .status(HttpStatus.OK)
                .payload(bookings)
                .build();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/history/{booking-id}")
    public ResponseEntity<ApiResponse<BookingDetail>> getBookingHistoryByBookingId(@PathVariable("booking-id") @Valid UUID bookingId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        Visitor visitor = profileService.getProfile(userId);
        BookingDetail bookings = bookingService.getBookingHistoryByBookingId(bookingId, visitor.getVisitorId());
        ApiResponse<BookingDetail> response = ApiResponse.<BookingDetail>builder()
                .success(true)
                .message("Booking retrieved successfully")
                .status(HttpStatus.OK)
                .payload(bookings)
                .build();

        return ResponseEntity.ok(response);
    }

}

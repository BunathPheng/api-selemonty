package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.BookingRequest;
import org.hrd.finalprojectmuseum.model.dto.request.PaymentAccountRequest;
import org.hrd.finalprojectmuseum.model.dto.request.RequestTourRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.BookingRequestV2;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.visitor.BookingV2;
import org.hrd.finalprojectmuseum.model.entity.PaymentCredential;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.enums.BookingType;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.model.enums.TicketType;
import org.hrd.finalprojectmuseum.service.*;
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
@RequestMapping("api/v1/bookings")
@RequiredArgsConstructor
public class BookingsController {
    private final BookingService bookingService;
    private final ProfileService profileService;
    private final AppUserService appUserService;
    private final ReviewService reviewService;
    private final ZoneService zoneService;

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @PostMapping("individual/{museum-id}")
    @Operation(summary = "Booking individual ticket for visitor")
    public ResponseEntity<ApiResponse<BookingV2>> IndividualBookingByMuseumId(
            @PathVariable("museum-id") UUID museumId,
            @RequestParam("ticketType") TicketType ticketType,
            @RequestBody @Valid BookingRequestV2 bookingRequest) {

        UUID visitorId = reviewService.getVisitorIdByUserId(appUserService.getUserId());
        BookingV2 booking = bookingService.bookingIndividualTicket(museumId, visitorId, ticketType, bookingRequest);

        ApiResponse<BookingV2> response = ApiResponse.<BookingV2>builder()
                .success(true)
                .message("Booking successfully")
                .status(HttpStatus.CREATED)
                .payload(booking)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @Operation(summary = "Use to get museum payment credential For KHQR payment. For only visitor")
    @GetMapping("/payment/{museum-id}")
    public ResponseEntity<ApiResponse<PaymentCredential>> getMuseumOwnerPaymentCredentialByMuseumId(
            @PathVariable("museum-id") @NotNull(message = "Museum ID is required") UUID museumId
    ) {
        PaymentCredential paymentCredential = profileService.getMuseumPaymentCredential(museumId);
        ApiResponse<PaymentCredential> response = ApiResponse.<PaymentCredential>builder()
                .success(true)
                .message("Museum payment credential has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(paymentCredential)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("tour/{museum-id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @Operation(summary = "For Request Tour. Only visitor can use")
    public ResponseEntity<ApiResponse<BookingV2>> tourRequest(
            @PathVariable("museum-id") UUID museumId,
            @RequestBody @Valid RequestTourRequest requestTourRequest){
        UUID visitorId = reviewService.getVisitorIdByUserId(appUserService.getUserId());
        BookingV2 tourRequest = bookingService.tourRequest(museumId, visitorId, requestTourRequest);

        ApiResponse<BookingV2> response = ApiResponse.<BookingV2>builder()
                .success(true)
                .message("Booking successfully")
                .status(HttpStatus.CREATED)
                .payload(tourRequest)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @Operation(
            summary = "For get booking by Booking ID for Visitor"
    )
    @GetMapping("/{booking-id}")
    public ResponseEntity<ApiResponse<BookingV2>> getBookingHistoryByBookingId(@PathVariable("booking-id") @Valid UUID bookingId) {
        UUID visitorId = reviewService.getVisitorIdByUserId(appUserService.getUserId());
        BookingV2 booking = bookingService.getBookingByVisitorIdV2(bookingId, visitorId);

        ApiResponse<BookingV2> response = ApiResponse.<BookingV2>builder()
                .success(true)
                .message("Booking retrieved successfully")
                .status(HttpStatus.OK)
                .payload(booking)
                .build();
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/filter")
    @PreAuthorize("hasRole('ROLE_VISITOR') or hasRole('ROLE_MUSEUM_OWNER')")
    @Operation(summary = "Visitor and Museum Owner can use this for get all booking history",
            description = "For Date must follow format (YYYY-MM-DD). If any filter dont want to use just leave it empty or search with letter."
    )
    public ResponseEntity<ApiResponse<List<BookingV2>>> getALlBookingHistoryAndFilter(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "bookingType", required = false) BookingType bookingType,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        AppUserRegister appUserRegister = appUserService.findUserByUserId(userId);
        List<BookingV2> bookings = null;

        Integer zeroBasedPage = page - 1;

        Integer totalItems = null;

        if (appUserRegister.getRole() == Role.ROLE_VISITOR){
            UUID visitorId = reviewService.getVisitorIdByUserId(appUserService.getUserId());
            totalItems = bookingService.countVisitorBookingHistory(visitorId, search, bookingType, zeroBasedPage, size, startDate, endDate);
            bookings = bookingService.getVisitorBookingHistory(visitorId, search, bookingType, zeroBasedPage, size , startDate, endDate);
        } else if (appUserRegister.getRole() == Role.ROLE_MUSEUM_OWNER) {
            UUID museumId = zoneService.getMuseumIdByUserId(appUserService.getUserId());
            totalItems = bookingService.countMuseumBookingHistory(museumId, search);
            bookings = bookingService.getMuseumBookingHistory(museumId, search, zeroBasedPage, size);
        }

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);

        ApiResponse<List<BookingV2>> response = ApiResponse.<List<BookingV2>>builder()
                .success(true)
                .message("Booking retrieved successfully")
                .status(HttpStatus.OK)
                .payload(bookings)
                .pagination(pagination)
                .build();
        return ResponseEntity.ok(response);
    }

}

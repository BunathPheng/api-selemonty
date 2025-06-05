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
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.entity.PaymentCredential;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.enums.BookingType;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.service.AppUserService;
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
import java.util.UUID;

@RestController
@RequestMapping("api/v1/bookings")
@RequiredArgsConstructor
public class BookingsController {
    private final BookingService bookingService;
    private final ProfileService profileService;
    private final AppUserService appUserService;

    @Operation(summary = "For booking a ticket. Only visitor can use.", description = "Need to input right ticket price and total price")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @PostMapping("/individual/{museum-id}")
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

    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "For RequestTour. Only visitor can use.")
    @PostMapping("/tour/{museum-id}")
    public ResponseEntity<ApiResponse<Booking>> requestTourByMuseumId(
            @PathVariable("museum-id") @Valid UUID museumId,
            @RequestBody @Valid RequestTourRequest requestTourRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        Visitor visitor = profileService.getProfile(userId);
        Booking booking = bookingService.requestTourByMuseumId(museumId, visitor.getVisitorId(), requestTourRequest);
        ApiResponse<Booking> response = ApiResponse.<Booking>builder()
                .success(true)
                .message("Booking successfully")
                .status(HttpStatus.CREATED)
                .payload(booking)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER') or hasRole('ROLE_VISITOR')")
    @Operation(
            summary = "For get all booking history",
            description = "For Date must follow format (YYYY-MM-DD). If any filter dont want to use just leave it empty."
    )

    @GetMapping()
    public ResponseEntity<ApiResponse<ListResponse<Booking>>> getBookingHistory(
            @RequestParam(value = "bookingType", required = false) BookingType bookingType,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        AppUserRegister appUserRegister = appUserService.findUserByUserId(userId);
        ListResponse<Booking> bookings = null;
        if (appUserRegister.getRole() == Role.ROLE_VISITOR){
            Visitor visitor = profileService.getProfile(userId);
            bookings = bookingService.getBookingHistoryByVisitorId(visitor.getVisitorId(), null, page, size, bookingType, null, null);
        } else if (appUserRegister.getRole() == Role.ROLE_MUSEUM_OWNER) {
            MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(userId);
            bookings = bookingService.getAllBookingByMuseumId(museumOwner.getMuseumId(), null, page, size, bookingType, null, null);
        }

        ApiResponse<ListResponse<Booking>> response = ApiResponse.<ListResponse<Booking>>builder()
                .success(true)
                .message("Bookings retrieved successfully")
                .status(HttpStatus.OK)
                .payload(bookings)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For get all booking history of a visitor with search, category and between of two date. MuseumOwner and Visitor can use.",
            description = "For Date must follow format (YYYY-MM-DD). If any filter dont want to use just leave it empty."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER') or hasRole('ROLE_VISITOR')")
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<ListResponse<Booking>>> getBookingHistoryByFilter(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "bookingType", required = false) BookingType bookingType,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        AppUserRegister appUserRegister = appUserService.findUserByUserId(userId);
        ListResponse<Booking> bookings = null;
        if (appUserRegister.getRole() == Role.ROLE_VISITOR){
            Visitor visitor = profileService.getProfile(userId);
            bookings = bookingService.getBookingHistoryByVisitorId(visitor.getVisitorId(), search, page, size, bookingType, startDate, endDate);
        } else if (appUserRegister.getRole() == Role.ROLE_MUSEUM_OWNER) {
            MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(userId);
            bookings = bookingService.getAllBookingByMuseumId(museumOwner.getMuseumId(), search, page, size, bookingType, startDate, endDate);
        }

        ApiResponse<ListResponse<Booking>> response = ApiResponse.<ListResponse<Booking>>builder()
                .success(true)
                .message("Bookings retrieved successfully")
                .status(HttpStatus.OK)
                .payload(bookings)
                .build();

        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER') or hasRole('ROLE_VISITOR')")
    @Operation(
            summary = "For get booking by Booking ID MuseumOwner and Visitor can use."
    )
    @GetMapping("/{booking-id}")
    public ResponseEntity<ApiResponse<Booking>> getBookingHistoryByBookingId(@PathVariable("booking-id") @Valid UUID bookingId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        AppUserRegister appUserRegister = appUserService.findUserByUserId(userId);
        Booking booking = null;
        if (appUserRegister.getRole() == Role.ROLE_VISITOR){

            Visitor visitor = profileService.getProfile(userId);
            booking = bookingService.getBookingByVisitorId(bookingId, visitor.getVisitorId());

        } else if (appUserRegister.getRole() == Role.ROLE_MUSEUM_OWNER) {

            MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(userId);
            booking = bookingService.getBookingByMuseumId(bookingId, museumOwner.getMuseumId());

        }
        ApiResponse<Booking> response = ApiResponse.<Booking>builder()
                .success(true)
                .message("Booking retrieved successfully")
                .status(HttpStatus.OK)
                .payload(booking)
                .build();
        return ResponseEntity.ok(response);
    }

}

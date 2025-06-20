package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.model.dto.request.RequestTourRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.BookingRequestV2;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.BookingAnalytics;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.visitor.BookingV2;
import org.hrd.finalprojectmuseum.model.entity.PaymentCredential;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.enums.BookingType;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.model.enums.TicketType;
import org.hrd.finalprojectmuseum.service.*;
import org.hrd.finalprojectmuseum.service.impl.AsyncEmailService;
import org.hrd.finalprojectmuseum.service.impl.QRCodeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.google.api.client.util.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingsController {
    private final BookingService bookingService;
    private final ProfileService profileService;
    private final AppUserService appUserService;
    private final ReviewService reviewService;
    private final ZoneService zoneService;
    private final QRCodeService qrCodeService;
    private final AsyncEmailService asyncEmailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_VISITOR')")
@PostMapping("individual/{museum-id}")
public ResponseEntity<ApiResponse<BookingV2>> IndividualBookingByMuseumId(
        @PathVariable("museum-id") UUID museumId,
        @RequestParam("ticketType") TicketType ticketType,
        @RequestBody @Valid BookingRequestV2 bookingRequest) {

    try {
        UUID visitorId = reviewService.getVisitorIdByUserId(appUserService.getUserId());
        BookingV2 booking = bookingService.bookingIndividualTicket(museumId, visitorId, ticketType, bookingRequest);

        // Generate QR code
        byte[] qrCodeBytes = qrCodeService.generateQRCodeFromBookingCode(booking.getQrCode());
        booking.setQRCodeData(qrCodeBytes, baseUrl);

        // GET VISITOR EMAIL AND PASS AS PARAMETER
        String visitorEmail = appUserService.getUserEmailByUserId(appUserService.getUserId());

        // Send email asynchronously
        asyncEmailService.sendBookingConfirmationEmailAsync(booking, visitorEmail, qrCodeBytes)
                .exceptionally(throwable -> {
                    log.error("Email sending failed for booking: {}", booking.getBookingId(), throwable);
                    return null;
                });

        asyncEmailService.sendBookingConfirmationEmailAsync(booking, visitorEmail, qrCodeBytes)
                .exceptionally(throwable -> {
                    log.error("Email sending failed for individual booking: {}", booking.getBookingId(), throwable);
                    return null;
                });

        ApiResponse<BookingV2> response = ApiResponse.<BookingV2>builder()
                .success(true)
                .message("Booking successfully created with QR code")
                .status(HttpStatus.CREATED)
                .payload(booking)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    } catch (Exception e) {
        log.error("Failed to create booking: {}", e.getMessage());

        ApiResponse<BookingV2> response = ApiResponse.<BookingV2>builder()
                .success(false)
                .message("Failed to create booking: " + e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
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
        try {
            UUID visitorId = reviewService.getVisitorIdByUserId(appUserService.getUserId());
            BookingV2 booking = bookingService.getBookingByVisitorIdV2(bookingId, visitorId);

            // Only generate QR code if it exists and is not null/empty
            if (booking.getQrCode() != null && !booking.getQrCode().trim().isEmpty()) {
                try {
                    byte[] qrCodeBytes = qrCodeService.generateQRCodeFromBookingCode(booking.getQrCode());
                    booking.setQRCodeData(qrCodeBytes, baseUrl);
                } catch (Exception qrException) {
                    // Log the QR generation error but don't fail the entire request
                    log.warn("Failed to generate QR code for booking {}: {}", bookingId, qrException.getMessage());
                    // QR code data will remain null, which is fine
                }
            }
            // If QR code doesn't exist (tour pending approval), just return booking without QR data

            ApiResponse<BookingV2> response = ApiResponse.<BookingV2>builder()
                    .success(true)
                    .message("Booking retrieved successfully")
                    .status(HttpStatus.OK)
                    .payload(booking)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to retrieve booking {}: {}", bookingId, e.getMessage());
            throw new AppBadRequestException("Restrict resource access, booking belong to other");
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/filter")
    @PreAuthorize("hasRole('ROLE_VISITOR') or hasRole('ROLE_MUSEUM_OWNER')")
    @Operation(summary = "Visitor and Museum Owner can use this for get all booking history",
            description = "For Date must follow format (YYYY-MM-DD). If any filter dont want to use just leave it empty or search with letter." +
                    "Museum Owner can use only search and pagination"
    )
    public ResponseEntity<ApiResponse<ListResponse<BookingV2>>> getALlBookingHistoryAndFilter(
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
        ListResponse<BookingV2> response = ListResponse.<BookingV2>builder()
                .items(bookings)
                .pagination(pagination)
                .build();

        ApiResponse<ListResponse<BookingV2>> listResponse = ApiResponse.<ListResponse<BookingV2>>builder()
                .success(true)
                .message("Booking retrieved successfully")
                .status(HttpStatus.OK)
                .payload(response)
                .build();
        return ResponseEntity.ok(listResponse);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MUSEUM_OWNER')")
    @GetMapping("/admin/museum/{museum-id}")
    public ResponseEntity<ApiResponse<BookingAnalytics>> getBookingAnalytics(@PathVariable("museum-id") UUID museumId){
        bookingService.getBookingAnalytics(museumId);

        return null;
    }

}

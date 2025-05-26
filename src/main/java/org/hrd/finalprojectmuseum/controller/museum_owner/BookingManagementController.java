package org.hrd.finalprojectmuseum.controller.museum_owner;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.BookingDetail;
import org.hrd.finalprojectmuseum.model.dto.response.BookingManagement;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.service.BookingService;
import org.hrd.finalprojectmuseum.service.MuseumOwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/booking/management")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
@SecurityRequirement(name = "bearerAuth")
public class BookingManagementController {

    private final BookingService bookingService;
    private final MuseumOwnerService museumOwnerService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "For get all booking of a museum with search and pagination. Only museum owner can use.")
    @GetMapping()
    public ResponseEntity<ApiResponse<ListResponse<BookingManagement>>> getBookingHistory(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = museumOwnerService.getMuseumOwnerByUserId(userId);

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

    @Operation(summary = "For check and verify booking ticket by bookingId which provide by qr scan. Only museum owner can use.")
    @PatchMapping("verify/qr")
    public ResponseEntity<ApiResponse<BookingDetail>> verifyScanQrCodeBookingId(@RequestParam("scan") @NotNull UUID bookingId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = museumOwnerService.getMuseumOwnerByUserId(userId);
        BookingDetail bookingDetail = bookingService.findScanBookingByBookingId(bookingId, museum.getMuseumId());
        String message;
        if (Objects.equals(bookingDetail.getTicketStatus(), "VALID")) {
            message = "QR code verified successfully. Booking is valid for entry.";
        } else if (Objects.equals(bookingDetail.getTicketStatus(), "USED")) {
            message = "This ticket has already been used for entry.";
        }else {
            message = "This ticket is expired.";
        }
        ApiResponse<BookingDetail> response = ApiResponse.<BookingDetail>builder()
                .success(true)
                .message(message)
                .status(HttpStatus.OK)
                .payload(bookingDetail)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "For check and verify booking ticket by code QR instead of scanning. Only museum owner can use.")
    @PatchMapping("verify/code")
    public ResponseEntity<ApiResponse<BookingDetail>> verifyQrCodeBookingId(@RequestParam("code-qr") @NotNull String codeQr) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = museumOwnerService.getMuseumOwnerByUserId(userId);
        BookingDetail bookingDetail = bookingService.findBookingByCodeQr(codeQr, museum.getMuseumId());
        String message;
        if (Objects.equals(bookingDetail.getTicketStatus(), "VALID")) {
            message = "QR code verified successfully. Booking is valid for entry.";
        } else if (Objects.equals(bookingDetail.getTicketStatus(), "USED")) {
            message = "This ticket has already been used for entry.";
        }else {
            message = "This ticket is expired.";
        }
        ApiResponse<BookingDetail> response = ApiResponse.<BookingDetail>builder()
                .success(true)
                .message(message)
                .status(HttpStatus.OK)
                .payload(bookingDetail)
                .build();

        return ResponseEntity.ok(response);
    }
}

package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.*;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.enums.MuseumStatus;
import org.hrd.finalprojectmuseum.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/museum")
@RequiredArgsConstructor
public class MuseumsController {
    private final ProfileService profileService;
    private final BookingService bookingService;
    private final TicketInfoService ticketInfoService;
    private final ScheduleService scheduleService;
    private final MuseumService museumService;

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @Operation(summary = "For check and verify booking ticket by bookingId which provide by qr scan. Only museum owner can use.")
    @PatchMapping("/management/verify/qr")
    public ResponseEntity<ApiResponse<Booking>> verifyScanQrCodeBookingId(@RequestParam("scan") @NotNull UUID bookingId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = profileService.getMuseumOwnerByUserId(userId);
        Booking bookingDetail = bookingService.findScanBookingByBookingId(bookingId, museum.getMuseumId());
        String message;
        if (Objects.equals(bookingDetail.getTicketStatus(), "VALID")) {
            message = "QR code verified successfully. Booking is valid for entry.";
        } else if (Objects.equals(bookingDetail.getTicketStatus(), "USED")) {
            message = "This ticket has already been used for entry.";
        }else {
            message = "This ticket is expired.";
        }
        ApiResponse<Booking> response = ApiResponse.<Booking>builder()
                .success(true)
                .message(message)
                .status(HttpStatus.OK)
                .payload(bookingDetail)
                .build();

        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @Operation(summary = "For check and verify booking ticket by code QR instead of scanning. Only museum owner can use.")
    @PatchMapping("/management/verify/code")
    public ResponseEntity<ApiResponse<Booking>> verifyQrCodeBookingId(@RequestParam("code-qr") @NotNull String codeQr) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = profileService.getMuseumOwnerByUserId(userId);
        Booking booking = bookingService.findBookingByCodeQr(codeQr, museum.getMuseumId());
        String message;
        if (Objects.equals(booking.getTicketStatus(), "VALID")) {
            message = "QR code verified successfully. Booking is valid for entry.";
        } else if (Objects.equals(booking.getTicketStatus(), "USED")) {
            message = "This ticket has already been used for entry.";
        }else {
            message = "This ticket is expired.";
        }
        ApiResponse<Booking> response = ApiResponse.<Booking>builder()
                .success(true)
                .message(message)
                .status(HttpStatus.OK)
                .payload(booking)
                .build();
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "For get all museums with all status type. Allowed only admin")
    @GetMapping()
    public ResponseEntity<ApiResponse<ListResponse<MuseumOwner>>> getAllMuseumOwners(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID museumCategoryId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size,
            @RequestParam("status") MuseumStatus museumStatus
            ) {
        ListResponse<MuseumOwner> museums = museumService.getAllMuseum(search, museumCategoryId, page, size, museumStatus);
        ApiResponse<ListResponse<MuseumOwner>> response = ApiResponse.<ListResponse<MuseumOwner>>builder()
                .success(true)
                .message("Museums has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(museums)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "For get all approved museums filter by distance. Allowed all role and guest")
    @GetMapping("/by-distance")
    public ResponseEntity<ApiResponse<List<MuseumWithDistanceResponse>>> getAllMuseumOwnersByLocation(
            @RequestParam(required = false) @Digits(integer = 4, fraction = 6, message = "Must be a number with up to 4 integer digits and 6 fractional digits") BigDecimal lat,
            @RequestParam(required = false) @Digits(integer = 4, fraction = 6, message = "Must be a number with up to 4 integer digits and 6 fractional digits") BigDecimal lng,
            @RequestParam(required = false) Integer distance
    ) {
        List<MuseumWithDistanceResponse> museums = museumService.getAllMuseumByLocation(lat, lng, distance);
        ApiResponse<List<MuseumWithDistanceResponse>> response = ApiResponse.<List<MuseumWithDistanceResponse>>builder()
                .success(true)
                .message("Museums has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(museums)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "For get all approved museums. Allowed all role and guest")
    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<ListResponse<MuseumOwner>>> getAllApprovedMuseumOwners(@RequestParam(required = false) UUID museumCategoryId, @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page, @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size) {
        ListResponse<MuseumOwner> museums = museumService.getAllApprovedMuseum(museumCategoryId, page, size);
        ApiResponse<ListResponse<MuseumOwner>> response = ApiResponse.<ListResponse<MuseumOwner>>builder()
                .success(true)
                .message("Approved Museums has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(museums)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/approve/{museum-id}")
    @Operation(summary = "For approve request museums. Allowed only admin")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> approveMuseum(@PathVariable("museum-id") @NotNull UUID museumId) {
        museumService.approveMuseum(museumId);
        //create ticket info but not set value data yet
        ticketInfoService.addTicketInfo(museumId);
        //create default schedule for museum
        scheduleService.addSchedule(museumId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Approve museum successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

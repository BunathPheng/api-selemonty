package org.hrd.finalprojectmuseum.controller;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumOwnerRequest;
import org.hrd.finalprojectmuseum.model.dto.response.*;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;
import org.hrd.finalprojectmuseum.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/museum")
@RequiredArgsConstructor
public class MuseumController {
    private final ProfileService profileService;
    private final BookingService bookingService;
    private final TicketInfoService ticketInfoService;
    private final ScheduleService scheduleService;
    private final MuseumService museumService;

    @Operation(summary = "For check and verify booking ticket by bookingId which provide by qr scan. Only museum owner can use.")
    @PatchMapping("/management/verify/qr")
    public ResponseEntity<ApiResponse<BookingDetail>> verifyScanQrCodeBookingId(@RequestParam("scan") @NotNull UUID bookingId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = profileService.getMuseumOwnerByUserId(userId);
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
    @PatchMapping("/management/verify/code")
    public ResponseEntity<ApiResponse<BookingDetail>> verifyQrCodeBookingId(@RequestParam("code-qr") @NotNull String codeQr) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museum = profileService.getMuseumOwnerByUserId(userId);
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

    @Operation(summary = "For get all museum and can add museumCategoryId to get museum", description = "Note for category we can leave it as null if we to get all museum by not filter by category")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<ListResponse<MuseumShortInfo>>> getAllMuseumOwners(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID museumCategoryId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size
    ) {
        ListResponse<MuseumShortInfo> museums = museumService.getAllMuseum(search, museumCategoryId, page, size);
        ApiResponse<ListResponse<MuseumShortInfo>> response = ApiResponse.<ListResponse<MuseumShortInfo>>builder()
                .success(true)
                .message("Museums has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(museums)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "For get all museum and can add museumCategoryId to get museum", description = "Note for category we can leave it as null if we to get all museum by not filter by category")
    @GetMapping("/by-location")
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "For get all approved museums and can add museumCategoryId to get museum", description = "Note for category we can leave it as null if we to get all approved museums by not filter by category")
    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<ListResponse<MuseumShortInfo>>> getAllApprovedMuseumOwners(@RequestParam(required = false) UUID museumCategoryId, @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page, @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size) {
        ListResponse<MuseumShortInfo> museums = museumService.getAllApprovedMuseum(museumCategoryId, page, size);
        ApiResponse<ListResponse<MuseumShortInfo>> response = ApiResponse.<ListResponse<MuseumShortInfo>>builder()
                .success(true)
                .message("Approved Museums has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(museums)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "For get all request museums and can add museumCategoryId to get museum", description = "Note for category we can leave it as null if we to get all request museums by not filter by category")
    @GetMapping("/request")
    public ResponseEntity<ApiResponse<ListResponse<MuseumShortInfo>>> viewRequestMuseum(@RequestParam(required = false) UUID museumCategoryId, @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page, @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size) {
        ListResponse<MuseumShortInfo> requestMuseums = museumService.getAllRequestMuseum(museumCategoryId, page, size);
        ApiResponse<ListResponse<MuseumShortInfo>> response = ApiResponse.<ListResponse<MuseumShortInfo>>builder()
                .success(true)
                .message("Request Museums has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(requestMuseums)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/approve/{museum-id}")
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

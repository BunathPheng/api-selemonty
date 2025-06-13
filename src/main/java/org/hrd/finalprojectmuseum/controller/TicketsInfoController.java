package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.TicketInfoRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.TicketInfo;
import org.hrd.finalprojectmuseum.model.entity.TicketStat;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.service.ProfileService;
import org.hrd.finalprojectmuseum.service.TicketInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/tickets")
@RequiredArgsConstructor
public class TicketsInfoController {

    private final TicketInfoService ticketInfoService;
    private final ProfileService profileService;

    @GetMapping("/{museum-id}")
    @Operation(summary = "For get Ticket Information and use for visitor role and museum owner role")
    public ResponseEntity<ApiResponse<TicketInfo>> getTicketInfoByMuseumId(@PathVariable("museum-id") @NotNull UUID museumId) {
        TicketInfo ticketInfo = ticketInfoService.getTicketInfoByMuseumId(museumId);
        ApiResponse<TicketInfo> response = ApiResponse.<TicketInfo>builder()
                .success(true)
                .message("Ticket has been fetched")
                .payload(ticketInfo)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @Operation(summary = "For get Ticket Information and use for museum owner role without required museum id")
    @GetMapping
    public ResponseEntity<ApiResponse<TicketInfo>> getTicketInfoByMuseumIdForMuseumOwner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(userId);
        TicketInfo ticketInfo = ticketInfoService.getTicketInfoByMuseumId(museumOwner.getMuseumId());
        ApiResponse<TicketInfo> response = ApiResponse.<TicketInfo>builder()
                .success(true)
                .message("Ticket has been fetched")
                .payload(ticketInfo)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @Operation(summary = "For update Ticket Information and use for museum owner role")
    @PutMapping()
    public ResponseEntity<ApiResponse<TicketInfo>> updateTicketInfo(@RequestBody TicketInfoRequest ticketInfoRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(userId);
        TicketInfo ticketInfo = ticketInfoService.updateTicketInfo(museumOwner.getMuseumId(), ticketInfoRequest);
        ApiResponse<TicketInfo> response = ApiResponse.<TicketInfo>builder()
                .success(true)
                .message("Ticket has been updated successfully")
                .payload(ticketInfo)
                .status(HttpStatus.CREATED)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    @Operation(summary = "Museum Dashboard ticket sold")
//    @GetMapping("/stat")
//    public ResponseEntity<ApiResponse<TicketStat>> getTicketInfoStat() {
//        TicketStat ticketStat = ticketInfoService.
//    }
}

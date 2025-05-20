package org.hrd.finalprojectmuseum.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ListMuseumResponse;
import org.hrd.finalprojectmuseum.service.MuseumManagementForAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/admin/museum")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class MuseumManagementForAdminController {

    private final MuseumManagementForAdminService museumManagementForAdminService;

    @Operation(summary = "For get all museum and can add museumCategoryId to get museum", description = "Note for category we can leave it as null if we to get all museum by not filter by category")
    @GetMapping("/museum")
    public ResponseEntity<ApiResponse<ListMuseumResponse>> getAllMuseumOwners(@RequestParam(required = false) UUID museumCategoryId, @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page, @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size) {
        ListMuseumResponse museums = museumManagementForAdminService.getAllMuseum(museumCategoryId, page, size);
        ApiResponse<ListMuseumResponse> response = ApiResponse.<ListMuseumResponse>builder()
                .success(true)
                .message("Museums has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(museums)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "For get all approved museums and can add museumCategoryId to get museum", description = "Note for category we can leave it as null if we to get all approved museums by not filter by category")
    @GetMapping("/museum/approved")
    public ResponseEntity<ApiResponse<ListMuseumResponse>> getAllApprovedMuseumOwners(@RequestParam(required = false) UUID museumCategoryId, @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page, @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size) {
        ListMuseumResponse museums = museumManagementForAdminService.getAllApprovedMuseum(museumCategoryId, page, size);
        ApiResponse<ListMuseumResponse> response = ApiResponse.<ListMuseumResponse>builder()
                .success(true)
                .message("Approved Museums has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(museums)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "For get all request museums and can add museumCategoryId to get museum", description = "Note for category we can leave it as null if we to get all request museums by not filter by category")
    @GetMapping("/museum/request")
    public ResponseEntity<ApiResponse<ListMuseumResponse>> viewRequestMuseum(@RequestParam(required = false) UUID museumCategoryId, @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page, @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size) {
        ListMuseumResponse requestMuseums = museumManagementForAdminService.getAllRequestMuseum(museumCategoryId, page, size);
        ApiResponse<ListMuseumResponse> response = ApiResponse.<ListMuseumResponse>builder()
                .success(true)
                .message("Request Museums has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(requestMuseums)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/museum/approve/{museum-id}")
    public ResponseEntity<ApiResponse<Void>> approveMuseum(@PathVariable("museum-id") @NotNull UUID museumId) {
        museumManagementForAdminService.approveMuseum(museumId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Approve museum successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

package org.hrd.finalprojectmuseum.controller;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumOwnerRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.service.MuseumOwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/museum-owner-profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
public class MuseumOwnerController {
    private final MuseumOwnerService museumOwnerService;

    @GetMapping()
    public ResponseEntity<ApiResponse<MuseumOwner>> getMuseumOwner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museumOwner = museumOwnerService.getMuseumOwnerByUserId(userId);
        ApiResponse<MuseumOwner> response = ApiResponse.<MuseumOwner>builder()
                .success(true)
                .message("Museum owner has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(museumOwner)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping()
    @Operation(summary = "Use for insert and update", description = "this endpoint can be use for insert more detail and also update any field. so you dont need to worry about field that dont want to update just leave it empty or null.")
    public ResponseEntity<ApiResponse<MuseumOwner>> updateMuseumOwner(@RequestBody @Valid MuseumOwnerRequest museumOwnerRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museumOwner = museumOwnerService.updateMuseumOwnerByUserId(userId, museumOwnerRequest);
        ApiResponse<MuseumOwner> response = ApiResponse.<MuseumOwner>builder()
                .success(true)
                .message("Museum owner has been updated successfully")
                .status(HttpStatus.OK)
                .payload(museumOwner)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping()
    public ResponseEntity<ApiResponse<Void>> deleteMuseumOwner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        museumOwnerService.deleteMuseumOwnerByUserId(userId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Museum owner has been delete successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/landscape")
    public ResponseEntity<ApiResponse<JSONObject>> addLandscape(@RequestBody JSONObject landscapeRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        JSONObject landscape = museumOwnerService.addLanscapeByUserId(userId, landscapeRequest);
        ApiResponse<JSONObject> response = ApiResponse.<JSONObject>builder()
                .success(true)
                .message("Museum owner has been updated successfully")
                .payload(landscape)
                .status(HttpStatus.CREATED)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/landscape")
    public ResponseEntity<ApiResponse<Void>> deleteLandscape(@RequestParam("keyName") String landscapeKey) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        museumOwnerService.deleteLandscapeByUserId(userId, landscapeKey);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Museum owner has been delete successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

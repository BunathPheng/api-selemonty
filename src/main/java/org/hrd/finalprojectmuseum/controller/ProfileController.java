package org.hrd.finalprojectmuseum.controller;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.PaymentAccountRequest;
import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumOwnerRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequestMapping("api/v1/profile")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // Museum Owner
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @Operation(summary = "Use to get museum profile. For only museum owner")
    @GetMapping("/museum-owner")
    public ResponseEntity<ApiResponse<MuseumOwner>> getMuseumOwner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(userId);
        ApiResponse<MuseumOwner> response = ApiResponse.<MuseumOwner>builder()
                .success(true)
                .message("Museum owner has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(museumOwner)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @PutMapping("/museum-owner")
    @Operation(summary = "Use to update museum profile. For only museum owner")
    public ResponseEntity<ApiResponse<MuseumOwner>> updateMuseumOwner(@RequestBody @Valid MuseumOwnerRequest museumOwnerRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museumOwner = profileService.updateMuseumOwnerByUserId(userId, museumOwnerRequest);
        ApiResponse<MuseumOwner> response = ApiResponse.<MuseumOwner>builder()
                .success(true)
                .message("Museum owner has been updated successfully")
                .status(HttpStatus.OK)
                .payload(museumOwner)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @Operation(summary = "Use to delete museum account. For only museum owner")
    @DeleteMapping("/museum-owner")
    public ResponseEntity<ApiResponse<Void>> deleteMuseumOwner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        profileService.deleteMuseumOwnerByUserId(userId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Museum owner has been delete successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @Operation(summary = "For add landscape", description = "Landscape is using JSONB so this endpoint use for add landscape")
    @PutMapping("/museum-owner/landscape")
    public ResponseEntity<ApiResponse<JSONObject>> addLandscape(@RequestBody JSONObject landscapeRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        JSONObject landscape = profileService.addLanscapeByUserId(userId, landscapeRequest);
        ApiResponse<JSONObject> response = ApiResponse.<JSONObject>builder()
                .success(true)
                .message("Landscape has been updated successfully")
                .payload(landscape)
                .status(HttpStatus.CREATED)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // For admin

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Admin>> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        Admin admin = profileService.getAdminByUserId(userId);
        ApiResponse<Admin> response = ApiResponse.<Admin>builder()
                .success(true)
                .message("Museum owner has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(admin)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Use for insert and update. Any field can be null if dont want to update", description = "this endpoint can be use for insert more detail and also update any field. so you dont need to worry about field that dont want to update just leave it empty or null.")
    @PutMapping("/admin")
    public ResponseEntity<ApiResponse<Admin>> updateProfile(@RequestBody @Valid AdminRequest adminRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        Admin admin = profileService.updateAdminByUserId(userId, adminRequest);
        ApiResponse<Admin> response = ApiResponse.<Admin>builder()
                .success(true)
                .message("Admin has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(admin)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    // visitor

    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @GetMapping("/visitor")
    public ResponseEntity<ApiResponse<Visitor>> getVisitorProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        Visitor visitor = profileService.getProfile(userId);
        ApiResponse<Visitor> response = ApiResponse.<Visitor>builder()
                .success(true)
                .message("fetch profile successfully")
                .payload(visitor)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @Operation(summary = "Use for update. For only Visitor", description = "this endpoint can be use for insert more detail and also update any field. so you dont need to worry about field that dont want to update just leave it empty or null.")
    @PutMapping("/visitor")
    public ResponseEntity<ApiResponse<Visitor>> updateVisitorProfile(@RequestBody @Valid VisitorRequest visitorRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        Visitor updateVisitor = profileService.updateVisitor(userId, visitorRequest);
        ApiResponse<Visitor> response = ApiResponse.<Visitor>builder()
                .success(true)
                .message("update profile successfully")
                .payload(updateVisitor)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @DeleteMapping("/visitor")
    public ResponseEntity<ApiResponse<Void>> deleteVisitor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        profileService.deleteVisitor(userId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("delete profile successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

}

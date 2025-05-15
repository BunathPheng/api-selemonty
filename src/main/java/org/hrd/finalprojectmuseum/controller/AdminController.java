package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.dto.request.admin.ChangePasswordRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.repository.AdminRepository;
import org.hrd.finalprojectmuseum.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/admin/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping()
    public ResponseEntity<ApiResponse<Admin>> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        Admin admin = adminService.getAdminByUserId(userId);
        ApiResponse<Admin> response = ApiResponse.<Admin>builder()
                .success(true)
                .message("Museum owner has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(admin)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping()
    public ResponseEntity<ApiResponse<Admin>> updateProfile(@RequestBody @Valid AdminRequest adminRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        Admin admin = adminService.updateAdminByUserId(userId, adminRequest);
        ApiResponse<Admin> response = ApiResponse.<Admin>builder()
                .success(true)
                .message("Admin has been fetched successfully")
                .status(HttpStatus.OK)
                .payload(admin)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@RequestBody @Valid ChangePasswordRequest passwordRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        adminService.updatePassword(userId, passwordRequest);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Password has been updated successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

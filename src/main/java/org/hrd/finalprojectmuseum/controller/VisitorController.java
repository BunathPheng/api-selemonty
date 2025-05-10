package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.service.VisitorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/profile")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_VISITOR')")
public class VisitorController {
    private final VisitorService visitorService;
    @GetMapping("get-profile")
    public ResponseEntity<ApiResponse<Visitor>> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        Visitor visitor = visitorService.getProfile(userId);
        ApiResponse<Visitor> response = ApiResponse.<Visitor>builder()
                .success(true)
                .message("fetch profile successfully")
                .payload(visitor)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/update-profile")
    public ResponseEntity<ApiResponse<Visitor>> updateProfile(@RequestBody @Valid VisitorRequest visitorRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        Visitor updateVisitor = visitorService.updateVisitor(userId, visitorRequest);
        ApiResponse<Visitor> response = ApiResponse.<Visitor>builder()
                .success(true)
                .message("update profile successfully")
                .payload(updateVisitor)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete-visitor")
    public ResponseEntity<ApiResponse<Void>> deleteVisitor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        visitorService.deleteVisitor(userId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("delete profile successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }
}

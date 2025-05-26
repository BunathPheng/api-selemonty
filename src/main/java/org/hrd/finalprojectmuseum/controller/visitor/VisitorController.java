package org.hrd.finalprojectmuseum.controller.visitor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("api/v1/visitor/profile")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_VISITOR')")
public class VisitorController {
    private final VisitorService visitorService;
    @GetMapping()
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

    @Operation(summary = "Use for insert and update. Any field can be null if dont want to update", description = "this endpoint can be use for insert more detail and also update any field. so you dont need to worry about field that dont want to update just leave it empty or null.")
    @PutMapping()
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

    @DeleteMapping()
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

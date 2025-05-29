package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.AppUser;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.service.AppUserService;
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
@RequestMapping("api/v1/visitor")
@SecurityRequirement(name = "bearerAuth")
public class VisitorController {

    private final AppUserService appUserService;
    private final VisitorService visitorService;

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @GetMapping("/museum")
    public ResponseEntity<ApiResponse<ListResponse<Visitor>>> getAllVisitorsOfMuseum(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size
    ){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        AppUserRegister user = appUserService.findUserByUserId(userId);
        ListResponse<Visitor> visitorListResponse = null;
        if (user.getRole() == Role.ROLE_MUSEUM_OWNER){
            visitorListResponse = visitorService.getVisitorByUserId(userId, search, page, size);
        }

        ApiResponse<ListResponse<Visitor>> response = ApiResponse.<ListResponse<Visitor>>builder()
                .success(true)
                .message("Visitors fetched successfully")
                .status(HttpStatus.OK)
                .payload(visitorListResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<ListResponse<Visitor>>> getAllVisitors(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size
    ){

        ListResponse<Visitor>  visitorListResponse = visitorService.getAllVisitor(search, page, size);

        ApiResponse<ListResponse<Visitor>> response = ApiResponse.<ListResponse<Visitor>>builder()
                .success(true)
                .message("Visitors fetched successfully")
                .status(HttpStatus.OK)
                .payload(visitorListResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

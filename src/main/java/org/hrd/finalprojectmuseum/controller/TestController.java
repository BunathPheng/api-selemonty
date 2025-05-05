package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearerAuth")

public class TestController {
    @PreAuthorize("hasRole('ROLE_VISITOR')")
    @GetMapping("/for-visitor")
    public String forVisitor() {
        return "test";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/for-super-admin")
    public String forSuperAdmin() {
        return "test";
    }

    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @GetMapping("/for-museum-owner")
    public String forMuseumOwner() {
        return "test";
    }
}

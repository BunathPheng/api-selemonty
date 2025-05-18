package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.hrd.finalprojectmuseum.model.entity.museum.ZoneCategory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/museumOwner")
public class ZoneController {

    @GetMapping
    @Operation(summary = "Get all zone categories")
    public ResponseEntity<List<ZoneCategory>> getAllZoneCategories() {
        return  null;
    }
}

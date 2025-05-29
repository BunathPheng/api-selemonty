package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumCategory;
import org.hrd.finalprojectmuseum.repository.MuseumRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/museum/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MuseumCategoriesController {

    private final MuseumRepository museumRepository;

    @GetMapping("/museum-category")
    public ResponseEntity<ApiResponse<List<MuseumCategory>>> getMuseumCategory() {
        List<MuseumCategory> museumCategories = museumRepository.getMuseumCategories();
        ApiResponse<List<MuseumCategory>> response = ApiResponse.<List<MuseumCategory>>builder()
                .message("Museum Categories has been fetched")
                .success(true)
                .status(HttpStatus.OK)
                .payload(museumCategories)
                .build();
        return ResponseEntity.ok(response);
    }
}

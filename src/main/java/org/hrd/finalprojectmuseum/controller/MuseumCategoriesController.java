package org.hrd.finalprojectmuseum.controller;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumCategory;
import org.hrd.finalprojectmuseum.service.MuseumOwnerService;
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
public class MuseumCategoriesController {

    private final MuseumOwnerService museumOwnerService;

    @GetMapping("/museum-category")
    public ResponseEntity<ApiResponse<List<MuseumCategory>>> getMuseumCategory() {
        List<MuseumCategory> museumCategories = museumOwnerService.getMuseumCategories();
        ApiResponse<List<MuseumCategory>> response = ApiResponse.<List<MuseumCategory>>builder()
                .message("Museum Categories has been fetched")
                .success(true)
                .status(HttpStatus.OK)
                .payload(museumCategories)
                .build();
        return ResponseEntity.ok(response);
    }
}

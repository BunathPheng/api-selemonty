package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.FollowerTrendChartResponse;
import org.hrd.finalprojectmuseum.model.entity.BookingChart;
import org.hrd.finalprojectmuseum.model.entity.MuseumChart;
import org.hrd.finalprojectmuseum.model.entity.VisitorChart;
import org.hrd.finalprojectmuseum.model.enums.YearFilter;
import org.hrd.finalprojectmuseum.service.ChartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/chart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ChartController {

    private final ChartService chartService;

    @Operation(summary = "Museum Owner dashboard follower and visitor chart")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @GetMapping("/follower")
    public ResponseEntity<ApiResponse<List<FollowerTrendChartResponse>>> getFollowerChart(
            @RequestParam(defaultValue = "THIS_YEAR", required = true) YearFilter yearFilter
    ){
        List<FollowerTrendChartResponse> followerChart = chartService.getFollowerChart(yearFilter);
        ApiResponse<List<FollowerTrendChartResponse>> apiResponse = ApiResponse.<List<FollowerTrendChartResponse>>builder()
                .success(true)
                .message("Follower chart fetched successfully")
                .payload(followerChart)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @Operation(summary = "Museum Owner dashboard booking chart")
    @PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
    @GetMapping("/booking")
    public ResponseEntity<ApiResponse<List<BookingChart>>> getBookingChart(
            @RequestParam(defaultValue = "THIS_YEAR") YearFilter yearFilter
    ){
        List<BookingChart> followerChart = chartService.getBookingChart(yearFilter);
        ApiResponse<List<BookingChart>> apiResponse = ApiResponse.<List<BookingChart>>builder()
                .success(true)
                .message("Booking chart fetched successfully")
                .payload(followerChart)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @Operation(summary = "Admin dashboard visitor chart")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/visitor")
    public ResponseEntity<ApiResponse<List<VisitorChart>>> getVisitorChart(
            @RequestParam(defaultValue = "THIS_YEAR") YearFilter yearFilter
    ){
        List<VisitorChart> followerChart = chartService.getVisitorChart(yearFilter);
        ApiResponse<List<VisitorChart>> apiResponse = ApiResponse.<List<VisitorChart>>builder()
                .success(true)
                .message("Visitor chart fetched successfully")
                .payload(followerChart)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @Operation(summary = "Admin dashboard museum chart")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/museum")
    public ResponseEntity<ApiResponse<MuseumChart>> getMuseumChart(
            @RequestParam(defaultValue = "THIS_YEAR") @NotNull(message = "YearFilter is required") YearFilter yearFilter
    ){
        MuseumChart followerChart = chartService.getMuseumChart(yearFilter);
        ApiResponse<MuseumChart> apiResponse = ApiResponse.<MuseumChart>builder()
                .success(true)
                .message("Visitor chart fetched successfully")
                .payload(followerChart)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}

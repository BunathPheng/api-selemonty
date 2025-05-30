package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.ScheduleRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.Schedule;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.service.ProfileService;
import org.hrd.finalprojectmuseum.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/museum/schedule")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
public class SchedulesController {

    private final ScheduleService scheduleService;
    private final ProfileService profileService;

    @GetMapping("/detail")
    @Operation(summary = "Get schedule of a week with 7 day")
    public ResponseEntity<ApiResponse<List<Schedule>>> getAllSchedulesDetailOfMuseum() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(userId);
        List<Schedule> schedules = scheduleService.getScheduleOfMuseum(museumOwner.getMuseumId());
        ApiResponse<List<Schedule>> response = ApiResponse.<List<Schedule>>builder()
                .success(true)
                .message("Schedules have been fetched successfully")
                .status(HttpStatus.OK)
                .payload(schedules)
                .build();
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{schedule-id}")
    @Operation(summary = "Get schedule by scheduleId")
    public ResponseEntity<ApiResponse<Schedule>> getAllSchedulesDetailOfMuseum(
            @PathVariable("schedule-id") @NotNull(message = "scheduleId is required") UUID scheduleId
    ) {
        Schedule schedules = scheduleService.getScheduleOfMuseumByScheduleId(scheduleId);
        ApiResponse<Schedule> response = ApiResponse.<Schedule>builder()
                .success(true)
                .message("Schedules with ID "+scheduleId+" have been fetched successfully")
                .status(HttpStatus.OK)
                .payload(schedules)
                .build();
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/grouped")
    public ResponseEntity<ApiResponse<List<Schedule>>> getGroupedSchedulesOfMuseum() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(userId);
        List<Schedule> schedules = scheduleService.getShortSchedulesOfMuseum(museumOwner.getMuseumId());
        ApiResponse<List<Schedule>> response = ApiResponse.<List<Schedule>>builder()
                .success(true)
                .message("Schedules have been fetched successfully")
                .status(HttpStatus.OK)
                .payload(schedules)
                .build();
        return  ResponseEntity.ok(response);
    }

    @Operation(summary = "For update schedule for any day of a week", description = "RequestBody is List of object and each object of a day of week so this allowed only 7 object. If List duplicate day the update will update as the latest one.")
    @PutMapping()
    public ResponseEntity<ApiResponse<List<Schedule>>> updateSchedule(
            @RequestBody @Valid List<ScheduleRequest> scheduleRequests) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UUID userId = UUID.fromString((String) auth.getCredentials());
            MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(userId);

            if (scheduleRequests.size() > 7) {
                throw new AppBadRequestException("Cannot have more than 7 schedule entries");
            }

            List<Schedule> responseSchedule = scheduleService.updateScheduleOfMuseum(
                    museumOwner.getMuseumId(),
                    scheduleRequests
            );

            ApiResponse<List<Schedule>> response = ApiResponse.<List<Schedule>>builder()
                    .success(true)
                    .message("Schedules have been updated successfully")
                    .status(HttpStatus.OK)
                    .payload(responseSchedule)
                    .build();
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            throw new AppBadRequestException("Invalid user authentication data");
        }
    }
}

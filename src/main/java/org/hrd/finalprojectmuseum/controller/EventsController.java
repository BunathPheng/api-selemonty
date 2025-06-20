package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.EventRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Event;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.enums.EventStatus;
import org.hrd.finalprojectmuseum.service.EventService;
import org.hrd.finalprojectmuseum.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequestMapping("api/v1/events")
@RequiredArgsConstructor
public class EventsController {

    private final EventService eventService;
    private final ProfileService profileService;

    @Operation(
            summary = "Get all event of all museums. Can use without authorize",
            description = "Use to get all event with pagination"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<ListResponse<Event>>> getAllEvents(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size,
            @RequestParam(name = "event-status") EventStatus eventStatus
            ) {
        ListResponse<Event> listEventResponse = eventService.findAllEvents(null, page, size, null, eventStatus);
        ApiResponse<ListResponse<Event>> response = ApiResponse.<ListResponse<Event>>builder()
                .success(true)
                .message("All events have been fetched")
                .status(HttpStatus.OK)
                .payload(listEventResponse)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<ListResponse<Event>>> getAllEventsWithFilter(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size,
            @RequestParam(required = false, name = "date-filter") LocalDate dateFilter,
            @RequestParam(name = "event-status") EventStatus eventStatus
    ) {
        ListResponse<Event> listEventResponse = eventService.findAllEvents(search, page, size, dateFilter, eventStatus);
        ApiResponse<ListResponse<Event>> response = ApiResponse.<ListResponse<Event>>builder()
                .success(true)
                .message("All events have been fetched")
                .status(HttpStatus.OK)
                .payload(listEventResponse)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/museum/{museum-id}")
    public ResponseEntity<ApiResponse<ListResponse<Event>>> getAllEventsByMuseumId(
                @PathVariable("museum-id") UUID museumId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size,
            @RequestParam(name = "event-status") EventStatus eventStatus
    ) {
        ListResponse<Event> listEventResponse = eventService.findAllEventsByMuseumId(museumId, null, page, size, null, eventStatus);
        ApiResponse<ListResponse<Event>> response = ApiResponse.<ListResponse<Event>>builder()
                .success(true)
                .message("All events have been fetched")
                .status(HttpStatus.OK)
                .payload(listEventResponse)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/museum/{museum-id}/filter")
    public ResponseEntity<ApiResponse<ListResponse<Event>>> getAllEventsWithFilterByMuseumId(
            @PathVariable("museum-id") UUID museumId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size,
            @RequestParam(required = false, name = "date-filter") LocalDate dateFilter,
            @RequestParam(name = "event-status") EventStatus eventStatus
    ) {
        ListResponse<Event> listEventResponse = eventService.findAllEventsByMuseumId(museumId, search, page, size, dateFilter, eventStatus);
        ApiResponse<ListResponse<Event>> response = ApiResponse.<ListResponse<Event>>builder()
                .success(true)
                .message("All events have been fetched")
                .status(HttpStatus.OK)
                .payload(listEventResponse)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get event by eventId")
    @GetMapping("/{event-id}")
    public ResponseEntity<ApiResponse<Event>> getEventById(
            @PathVariable("event-id") @NotNull UUID eventId
    ) {
        Event event = eventService.findEventsByEventId(eventId);
        ApiResponse<Event> response = ApiResponse.<Event>builder()
                .success(true)
                .message("All events have been fetched")
                .status(HttpStatus.OK)
                .payload(event)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('MUSEUM_OWNER')")
    @Operation(summary = "Create a new event. For museum owner role only")
    @PostMapping()
    public ResponseEntity<ApiResponse<Event>> createNewEvent(@RequestBody @Valid EventRequest eventRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(userId);
        Event event = eventService.addNewEvent(museumOwner, eventRequest);
        ApiResponse<Event> response = ApiResponse.<Event>builder()
                .success(true)
                .message("Event has been created successfully")
                .status(HttpStatus.CREATED)
                .payload(event)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('MUSEUM_OWNER')")
    @Operation(summary = "Update an event by event ID. For museum owner role only")
    @PutMapping("/{event-id}")
    public ResponseEntity<ApiResponse<Event>> updateEventByEventId(
            @RequestBody @Valid EventRequest eventRequest,
            @PathVariable("event-id") @NotNull UUID eventId
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(userId);
        Event event = eventService.updateEventByEventId(eventId, eventRequest, museumOwner.getMuseumId());
        ApiResponse<Event> response = ApiResponse.<Event>builder()
                .success(true)
                .message("Event has been updated successfully")
                .status(HttpStatus.OK)
                .payload(event)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('MUSEUM_OWNER')")
    @Operation(summary = "Soft delete an event by updating delete status. For museum owner role only")
    @DeleteMapping("/{event-id}")
    public ResponseEntity<ApiResponse<Void>> updateDeleteStatus(@PathVariable("event-id") @NotNull UUID eventId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(userId);
        eventService.updateDeleteStatus(eventId, museumOwner.getMuseumId());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Event has been deleted successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
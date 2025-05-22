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
import org.hrd.finalprojectmuseum.service.EventService;
import org.hrd.finalprojectmuseum.service.MuseumOwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("api/v1/event")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_MUSEUM_OWNER')")
public class EventController {

    private final EventService eventService;
    private final MuseumOwnerService museumOwnerService;

    @Operation(
            summary = "Get all event of all museums",
            description = "Use to get all event with pagination"
    )
    @GetMapping()
    @PreAuthorize("hasRole('ROLE_VISITOR')")
    public ResponseEntity<ApiResponse<ListResponse<Event>>> getAllEvents(@RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page, @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size) {
        ListResponse<Event> listEventResponse = eventService.findAllEvents(page, size);
        ApiResponse<ListResponse<Event>> response = ApiResponse.<ListResponse<Event>>builder()
                .success(true)
                .message("All events have been fetched")
                .status(HttpStatus.OK)
                .payload(listEventResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get all event of a museum",
            description = "Use to get all event of museum with pagination. Required museumId"
    )
    @GetMapping("museum/{museum-id}")
    @PreAuthorize("hasRole('ROLE_VISITOR')")
    public ResponseEntity<ApiResponse<ListResponse<Event>>> getAllEventsByMuseumId(@PathVariable("museum-id") @NotNull UUID museumId, @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be greater than 0") Integer page, @RequestParam(defaultValue = "10") @Min(value = 1, message = "must be greater than 0") Integer size) {
        ListResponse<Event> listEventResponse = eventService.findAllEventsByMuseumId(museumId, page, size);
        ApiResponse<ListResponse<Event>> response = ApiResponse.<ListResponse<Event>>builder()
                .success(true)
                .message("All events have been fetched")
                .status(HttpStatus.OK)
                .payload(listEventResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Use to get event by using eventId. For visitor and museum owner role")
    @GetMapping("/{event-id}")
    @PreAuthorize("hasRole('ROLE_VISITOR')")
    public ResponseEntity<ApiResponse<Event>> getEventsByEventId(@PathVariable("event-id") @NotNull UUID eventId) {
        Event event = eventService.findEventsByEventId(eventId);
        ApiResponse<Event> response = ApiResponse.<Event>builder()
                .success(true)
                .message("All events have been fetched")
                .status(HttpStatus.OK)
                .payload(event)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Use to create new event. For museum owner role only")
    @PostMapping()
    public ResponseEntity<ApiResponse<Event>> createNewEvent(@RequestBody @Valid EventRequest eventRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        MuseumOwner museumOwner = museumOwnerService.getMuseumOwnerByUserId(userId);
        Event event = eventService.addNewEvent(museumOwner, eventRequest);
        ApiResponse<Event> response = ApiResponse.<Event>builder()
                .success(true)
                .message("Event have been created successfully")
                .status(HttpStatus.CREATED)
                .payload(event)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Use to update event. For museum owner role only")
    @PutMapping("/{event-id}")
    public ResponseEntity<ApiResponse<Event>> updateEventByEventId(@RequestBody @Valid EventRequest eventRequest, @PathVariable("event-id") @NotNull UUID eventId) {
        Event event = eventService.updateEventByEventId(eventId, eventRequest);
        ApiResponse<Event> response = ApiResponse.<Event>builder()
                .success(true)
                .message("All events have been updated successfully")
                .status(HttpStatus.OK)
                .payload(event)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Use to delete event by update delete status to true new event. For museum owner role only")
    @PatchMapping("/{event-id}")
    public ResponseEntity<ApiResponse<Event>> updateDeleteStatus(@PathVariable("event-id") @NotNull UUID eventId) {
        eventService.updateDeleteStatus(eventId);
        ApiResponse<Event> response = ApiResponse.<Event>builder()
                .success(true)
                .message("Event have been updated to deleted successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

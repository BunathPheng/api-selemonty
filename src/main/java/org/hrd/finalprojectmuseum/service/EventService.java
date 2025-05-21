package org.hrd.finalprojectmuseum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.request.EventRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Event;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;

import java.util.UUID;

public interface EventService {
    ListResponse<Event> findAllEvents(Integer page, Integer size);

    ListResponse<Event> findAllEventsByMuseumId(UUID museumId, Integer page, Integer size);

    Event findEventsByEventId(UUID eventId);

    Event addNewEvent(MuseumOwner museumOwner, EventRequest eventRequest);

    Event updateEventByEventId(UUID eventId, EventRequest eventRequest);

    void updateDeleteStatus(UUID eventId);
}

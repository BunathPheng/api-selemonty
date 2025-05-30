package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.dto.request.EventRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Event;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;

import java.time.LocalDate;
import java.util.UUID;

public interface EventService {
    ListResponse<Event> findAllEvents(String search, Integer page, Integer size, LocalDate dateFiler);

    Event findEventsByEventId(UUID eventId);

    Event addNewEvent(MuseumOwner museumOwner, EventRequest eventRequest);

    Event updateEventByEventId(UUID eventId, EventRequest eventRequest, UUID museumId);

    void updateDeleteStatus(UUID eventId, UUID museumId);
}

package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.EventRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Event;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;
import org.hrd.finalprojectmuseum.repository.EventRepository;
import org.hrd.finalprojectmuseum.service.EventService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public ListResponse<Event> findAllEvents(Integer page, Integer size) {
        Integer totalItems = eventRepository.countAllEvent();

        List<Event> events = eventRepository.findAllEvents(page, size);
        for (Event event : events) {
            event.updateStatus();
        }
        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);


        return ListResponse.<Event>builder()
                .items(events)
                .pagination(pagination)
                .build();
    }

    @Override
    public ListResponse<Event> findAllEventsByMuseumId(UUID museumId, Integer page, Integer size) {
        Integer totalItems = eventRepository.countAllEventByMuseumId(museumId);

        List<Event> events = eventRepository.findAllEventsByMuseumId(museumId, page, size);
        for (Event event : events) {
            event.updateStatus();
        }
        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);

        return ListResponse.<Event>builder()
                .items(events)
                .pagination(pagination)
                .build();
    }

    @Override
    public Event findEventsByEventId(UUID eventId) {
        Event event = eventRepository.findEventByEventId(eventId);
        if (event == null) {
            throw new AppNotFoundException("Event with ID " + eventId + " does not exist");
        } else if (event.getDeleted()) {
            throw new AppBadRequestException("Action failed! Event with ID " + eventId + " had been deleted");
        }
        event.updateStatus();
        return event;
    }

    @Override
    public Event addNewEvent(MuseumOwner museumOwner, EventRequest eventRequest) {
        if (eventRequest.getCurator().isEmpty()) {
            eventRequest.setCurator(museumOwner.getName());
        }
        Event event = eventRepository.insertEvent(museumOwner.getMuseumId(), eventRequest);
        event.updateStatus();
        return event;
    }

    @Override
    public Event updateEventByEventId(UUID eventId, EventRequest eventRequest) {
        findEventsByEventId(eventId);
        Event updatedEvent = eventRepository.updateEventByEventId(eventId, eventRequest, LocalDateTime.now());
        updatedEvent.updateStatus();
        return updatedEvent;
    }

    @Override
    public void updateDeleteStatus(UUID eventId) {
        findEventsByEventId(eventId);
        eventRepository.updateDeleteStatus(eventId, LocalDateTime.now());
    }
}

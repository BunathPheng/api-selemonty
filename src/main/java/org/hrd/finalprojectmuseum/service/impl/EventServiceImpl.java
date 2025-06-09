package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.EventRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Event;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.enums.EventStatus;
import org.hrd.finalprojectmuseum.repository.EventRepository;
import org.hrd.finalprojectmuseum.service.EventService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public ListResponse<Event> findAllEvents(String search, Integer page, Integer size, LocalDate dateFiler, EventStatus eventStatus) {
        search = search == null ? "" : search;
        Integer totalItems;
        List<Event> events;
        if (eventStatus == EventStatus.ALL) {
            if (dateFiler == null) {
                events = eventRepository.findAllEvents(search, page, size);
                totalItems = eventRepository.countAllEvent(search);
            } else {
                events = eventRepository.findAllEventsWithDateFilter(search, page, size, dateFiler);
                totalItems = eventRepository.countAllEventWithFilter(search, dateFiler);
            }
        }else if(eventStatus == EventStatus.AVAILABLE ){
            if (dateFiler == null) {
                events = eventRepository.findAllEventsAvailable(search, page, size);
                totalItems = eventRepository.countAllEventAvailable(search);
            } else {
                events = eventRepository.findAllEventsWithDateFilterAvailable(search, page, size, dateFiler);
                totalItems = eventRepository.countAllEventWithFilterAvailable(search, dateFiler);
            }
        }else if(eventStatus == EventStatus.UPCOMING ){
            if (dateFiler == null) {
                events = eventRepository.findAllEventsUpComing(search, page, size);
                totalItems = eventRepository.countAllEventUpComing(search);
            } else {
                events = eventRepository.findAllEventsWithDateFilterUpComing(search, page, size, dateFiler);
                totalItems = eventRepository.countAllEventWithFilterUpComing(search, dateFiler);
            }
        }else if(eventStatus == EventStatus.ONGOING ){
            if (dateFiler == null) {
                events = eventRepository.findAllEventsOnGoing(search, page, size);
                totalItems = eventRepository.countAllEventOnGoing(search);
            } else {
                events = eventRepository.findAllEventsWithDateFilterOngoing(search, page, size, dateFiler);
                totalItems = eventRepository.countAllEventWithFilterOnGoing(search, dateFiler);
            }
        }else {
            if (dateFiler == null) {
                events = eventRepository.findAllEventsEnded(search, page, size);
                totalItems = eventRepository.countAllEventEnded(search);
            } else {
                events = eventRepository.findAllEventsWithDateFilterEnded(search, page, size, dateFiler);
                totalItems = eventRepository.countAllEventWithFilterEnded(search, dateFiler);
            }
        }
        for (Event event : events) {
            event.updateStatus();
        }
        Pagination pagination = new Pagination();
        totalItems = totalItems == null ? 0 : totalItems;
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
    public Event updateEventByEventId(UUID eventId, EventRequest eventRequest, UUID museumId) {
        Event event = findEventsByEventId(eventId);
        if (event == null){
            throw new AppNotFoundException("Event with ID " + eventId + " does not exist");
        }
        if (!event.getMuseum().getMuseumId().equals(museumId)){
            throw new AppBadRequestException("Event belongs to a other museum. You cannot update this event");
        }
        Event updatedEvent = eventRepository.updateEventByEventId(eventId, eventRequest, LocalDateTime.now());
        updatedEvent.updateStatus();
        return updatedEvent;
    }

    @Override
    public void updateDeleteStatus(UUID eventId, UUID museumId) {
        Event event = findEventsByEventId(eventId);
        if (event == null){
            throw new AppNotFoundException("Event with ID " + eventId + " does not exist");
        }
        if (!event.getMuseum().getMuseumId().equals(museumId)){
            throw new AppBadRequestException("Event belongs to a other museum. You cannot update this event");
        }
        findEventsByEventId(eventId);
        eventRepository.updateDeleteStatus(eventId, LocalDateTime.now());
    }

    @Override
    public ListResponse<Event> findAllEventsByMuseumId(UUID museumId, String search, Integer page, Integer size, LocalDate dateFiler, EventStatus eventStatus) {
        search = search == null ? "" : search;
        Integer totalItems;
        List<Event> events;

        if (eventStatus == EventStatus.ALL) {
            if (dateFiler == null) {
                events = eventRepository.findAllEventsByMuseumId(museumId, search, page, size);
                totalItems = eventRepository.countAllEventByMuseumId(museumId, search);
            } else {
                events = eventRepository.findAllEventsWithDateFilterByMuseumId(museumId, search, page, size, dateFiler);
                totalItems = eventRepository.countAllEventWithFilterByMuseumId(museumId, search, dateFiler);
            }
        } else if (eventStatus == EventStatus.AVAILABLE) {
            if (dateFiler == null) {
                events = eventRepository.findAllEventsAvailableByMuseumId(museumId, search, page, size);
                totalItems = eventRepository.countAllEventAvailableByMuseumId(museumId, search);
            } else {
                events = eventRepository.findAllEventsWithDateFilterAvailableByMuseumId(museumId, search, page, size, dateFiler);
                totalItems = eventRepository.countAllEventWithFilterAvailableByMuseumId(museumId, search, dateFiler);
            }
        } else if (eventStatus == EventStatus.UPCOMING) {
            if (dateFiler == null) {
                events = eventRepository.findAllEventsUpComingByMuseumId(museumId, search, page, size);
                totalItems = eventRepository.countAllEventUpComingByMuseumId(museumId, search);
            } else {
                events = eventRepository.findAllEventsWithDateFilterUpComingByMuseumId(museumId, search, page, size, dateFiler);
                totalItems = eventRepository.countAllEventWithFilterUpComingByMuseumId(museumId, search, dateFiler);
            }
        } else if (eventStatus == EventStatus.ONGOING) {
            if (dateFiler == null) {
                events = eventRepository.findAllEventsOnGoingByMuseumId(museumId, search, page, size);
                totalItems = eventRepository.countAllEventOnGoingByMuseumId(museumId, search);
            } else {
                events = eventRepository.findAllEventsWithDateFilterOngoingByMuseumId(museumId, search, page, size, dateFiler);
                totalItems = eventRepository.countAllEventWithFilterOnGoingByMuseumId(museumId, search, dateFiler);
            }
        } else {
            if (dateFiler == null) {
                events = eventRepository.findAllEventsEndedByMuseumId(museumId, search, page, size);
                totalItems = eventRepository.countAllEventEndedByMuseumId(museumId, search);
            } else {
                events = eventRepository.findAllEventsWithDateFilterEndedByMuseumId(museumId, search, page, size, dateFiler);
                totalItems = eventRepository.countAllEventWithFilterEndedByMuseumId(museumId, search, dateFiler);
            }
        }

        for (Event event : events) {
            event.updateStatus();
        }

        Pagination pagination = new Pagination();
        totalItems = totalItems == null ? 0 : totalItems;
        pagination = pagination.calculatePagination(totalItems, page, size);

        return ListResponse.<Event>builder()
                .items(events)
                .pagination(pagination)
                .build();
    }
}

package org.hrd.finalprojectmuseum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.ScheduleRequest;
import org.hrd.finalprojectmuseum.model.entity.Schedule;
import org.hrd.finalprojectmuseum.model.enums.DayOfWeek;

import java.util.List;
import java.util.UUID;

public interface ScheduleService {
    List<Schedule> getScheduleOfMuseum(UUID museumId);

    void addSchedule(UUID museumId);

    List<Schedule> updateScheduleOfMuseum(UUID museumId, List<ScheduleRequest> scheduleRequests);

    Schedule getScheduleOfMuseumByScheduleId(UUID scheduleId);

    List<Schedule> getShortSchedulesOfMuseum(UUID museumId);

    Schedule getScheduleByDay(UUID museumId, String day);
}

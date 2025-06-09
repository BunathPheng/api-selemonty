package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.ScheduleRequest;
import org.hrd.finalprojectmuseum.model.entity.Schedule;
import org.hrd.finalprojectmuseum.model.enums.DayOfWeek;
import org.hrd.finalprojectmuseum.repository.ScheduleRepository;
import org.hrd.finalprojectmuseum.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Override
    public List<Schedule> getScheduleOfMuseum(UUID museumId) {
        return scheduleRepository.findScheduleOfMuseum(museumId);
    }

    @Override
    public void addSchedule(UUID museumId) {
        for(DayOfWeek dayOfWeek : DayOfWeek.values()) {
            scheduleRepository.insertSchedule(museumId, dayOfWeek.name());
        }
    }

    @Override
    public List<Schedule> updateScheduleOfMuseum(UUID museumId, List<ScheduleRequest> scheduleRequests) {
        List<Schedule> responseSchedule = new ArrayList<>();
        for (ScheduleRequest schedule : scheduleRequests) {
            Schedule updatedSchedule = scheduleRepository.modifySchedule(museumId, schedule, LocalDateTime.now());
            responseSchedule.add(updatedSchedule);
        }
        return responseSchedule;
    }

    @Override
    public Schedule getScheduleOfMuseumByScheduleId(UUID scheduleId) {
        Schedule schedule = scheduleRepository.findScheduleOfMuseumByScheduleId(scheduleId);
        if (schedule == null) {
            throw new AppNotFoundException("Schedule with ID "+scheduleId+" not exists");
        }
        return schedule;
    }

    @Override
    public List<Schedule> getShortSchedulesOfMuseum(UUID museumId) {
        List<Schedule> schedules = scheduleRepository.findScheduleOfMuseum(museumId);
        if (schedules.isEmpty()) {
            return new ArrayList<>();
        }

        List<Schedule> groupedSchedules = new ArrayList<>();

        int i = 0;
        while (i < schedules.size()) {
            Schedule currentSchedule = schedules.get(i);
            String startDay = currentSchedule.getDayOfWeek();
            String endDay = startDay;

            int j = i + 1;
            while (j < schedules.size() && hasSameSchedule(schedules.get(i), schedules.get(j))) {
                endDay = schedules.get(j).getDayOfWeek();
                j++;
            }

            Schedule groupedSchedule = new Schedule();
            groupedSchedule.setScheduleId(currentSchedule.getScheduleId());
            groupedSchedule.setOpeningTime(currentSchedule.getOpeningTime());
            groupedSchedule.setClosingTime(currentSchedule.getClosingTime());
            groupedSchedule.setDayOff(currentSchedule.getDayOff());
            groupedSchedule.setCreatedAt(currentSchedule.getCreatedAt());
            groupedSchedule.setUpdatedAt(currentSchedule.getUpdatedAt());

            if (startDay.equals(endDay)) {
                groupedSchedule.setDayOfWeek(startDay);
            } else {
                groupedSchedule.setDayOfWeek(startDay + " - " + endDay);
            }

            groupedSchedules.add(groupedSchedule);

            i = j;
        }

        return groupedSchedules;
    }

    @Override
    public Schedule getScheduleByDay(UUID museumId, String day) {
        return scheduleRepository.findScheduleOfMuseumByDay(museumId, day);
    }

    private boolean hasSameSchedule(Schedule s1, Schedule s2) {
        if (s1.getDayOff() && s2.getDayOff()) {
            return true;
        }

        if (!s1.getDayOff() && !s2.getDayOff()) {
            return Objects.equals(s1.getOpeningTime(), s2.getOpeningTime()) &&
                    Objects.equals(s1.getClosingTime(), s2.getClosingTime());
        }

        return false;
    }
}

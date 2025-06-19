package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.ScheduleRequest;
import org.hrd.finalprojectmuseum.model.entity.Schedule;
import org.hrd.finalprojectmuseum.model.enums.DayOfWeek;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface ScheduleRepository {

    @Results(id = "scheduleMapper", value = {
            @Result(property = "scheduleId", column = "schedule_id"),
            @Result(property = "dayOfWeek", column = "day"),
            @Result(property = "openingTime", column = "opening_time"),
            @Result(property = "closingTime", column = "closing_time"),
            @Result(property = "dayOff", column = "day_off"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
    })
    @Select("""
        SELECT schedule_id, day, opening_time, closing_time, day_off, created_at, updated_at
        FROM schedules
        WHERE museum_id = #{museumId}::UUID
        ORDER BY
            CASE day
                WHEN 'MONDAY' THEN 1
                WHEN 'TUESDAY' THEN 2
                WHEN 'WEDNESDAY' THEN 3
                WHEN 'THURSDAY' THEN 4
                WHEN 'FRIDAY' THEN 5
                WHEN 'SATURDAY' THEN 6
                WHEN 'SUNDAY' THEN 7
            END;
    """)
    List<Schedule> findScheduleOfMuseum(UUID museumId);

    @Insert("""
        INSERT INTO schedules (museum_id, day, day_off) VALUES (#{museumId}::UUID, #{dayOfWeek}, true);
    """)
    void insertSchedule(UUID museumId, String dayOfWeek);

    @ResultMap("scheduleMapper")
    @Select("""
        UPDATE schedules
        SET opening_time = #{schedule.openingTime},
            closing_time = #{schedule.closingTime},
            day_off = #{schedule.dayOff},
            updated_at = #{updatedAt}
        WHERE museum_id = #{museumId}::UUID
        AND day = #{schedule.dayOfWeek}
        RETURNING *;
    """)
    Schedule modifySchedule(UUID museumId, @Param("schedule") ScheduleRequest scheduleRequest, LocalDateTime updatedAt);

    @ResultMap("scheduleMapper")
    @Select("""
        SELECT schedule_id, day, opening_time, closing_time, day_off, created_at, updated_at FROM schedules WHERE schedule_id = #{scheduleId}::UUID;
    """)
    Schedule findScheduleOfMuseumByScheduleId(UUID scheduleId);

    @ResultMap("scheduleMapper")
    @Select("""
        SELECT schedule_id, day, opening_time, closing_time, day_off, created_at, updated_at FROM schedules
        WHERE museum_id = #{museumId}::UUID AND day = #{day};
    """)
    Schedule findScheduleOfMuseumByDay(UUID museumId, String day);
}

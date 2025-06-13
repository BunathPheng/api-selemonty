package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.dto.request.EventRequest;
import org.hrd.finalprojectmuseum.model.entity.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface EventRepository {

    @Select("""
        SELECT count(*) FROM events WHERE title ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllEvent(String search);

    @Results(id = "eventMapper", value = {
            @Result(property = "eventId", column = "event_id"),
            @Result(property = "museum", column = "museum_id", javaType = UUID.class, jdbcType = JdbcType.VARCHAR,
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.MuseumRepository.findMuseumByMuseumId")
            ),
            @Result(property = "title", column = "title"),
            @Result(property = "subTitle", column = "sub_title"),
            @Result(property = "content", column = "content"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "imageLinks", column = "image_links"),
            @Result(property = "curator", column = "curator"),
            @Result(property = "accessibilityNote", column = "accessibility_note"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "update_at"),
            @Result(property = "deleted", column = "is_deleted"),
    })
    @Select("""
        SELECT * FROM events
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEvents(@Param("search") String search, @Param("page") Integer page, @Param("size") Integer size);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEventsWithDateFilter(@Param("search") String search, @Param("page") Integer page, @Param("size") Integer size, @Param("dateFilter") LocalDate dateFilter);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events WHERE museum_id = #{museumId}::UUID AND is_deleted = false
                             AND title ILIKE CONCAT('%', #{search}, '%')
                             ORDER BY start_date
                             offset (#{page}-1)* #{size} limit #{size}
    """)
    List<Event> findAllEventsByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("page") Integer page, @Param("size") Integer size);

    // Fixed parameter order to match service call
    @Select("""
        SELECT count(*) FROM events WHERE museum_id = #{museumId}::UUID
        AND is_deleted = false AND title ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllEventByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search);

    // NEW: Museum ID with date filter
    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events
        WHERE museum_id = #{museumId}::UUID
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEventsWithDateFilterByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("page") Integer page, @Param("size") Integer size, @Param("dateFilter") LocalDate dateFilter);

    @Select("""
        SELECT count(*) FROM events WHERE museum_id = #{museumId}::UUID
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
    """)
    Integer countAllEventWithFilterByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("dateFilter") LocalDate dateFilter);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events WHERE event_id = #{eventId}::UUID
    """)
    Event findEventByEventId(UUID eventId);

    @ResultMap("eventMapper")
    @Select("""
        INSERT into events VALUES (default, #{museumId}::UUID, #{event.title}, #{event.subTitle},
                                   #{event.content}, #{event.startDate}, #{event.endDate}, #{event.imageLinks}::JSONB,
                                   #{event.curator}, #{event.accessibilityNote}::TEXT, default, default, default) RETURNING *
    """)
    Event insertEvent(UUID museumId, @Param("event") EventRequest eventRequest);

    @ResultMap("eventMapper")
    @Select("""
        UPDATE events SET title = #{event.title}, sub_title = #{event.subTitle},
        content = #{event.content}, start_date = #{event.startDate}, end_date = #{event.endDate},
        image_links = #{event.imageLinks}::JSONB, curator = #{event.curator}, accessibility_note = #{event.accessibilityNote}, updated_at = #{updatedAt}
        WHERE event_id = #{eventId}::UUID AND is_deleted = false RETURNING *
    """)
    Event updateEventByEventId(UUID eventId, @Param("event") EventRequest eventRequest, LocalDateTime updatedAt);

    @Update("""
        UPDATE events SET is_deleted = true, updated_at = #{updatedAt} WHERE event_id = #{eventId}::UUID AND is_deleted = false
    """)
    void updateDeleteStatus(UUID eventId, LocalDateTime updatedAt);

    @Select("""
        SELECT count(*) FROM events WHERE title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
    """)
    Integer countAllEventWithFilter(String search, LocalDate dateFilter);

    // AVAILABLE EVENTS
    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND (
            (start_date <= NOW() AND end_date >= NOW())  -- ongoing
            OR (start_date > NOW())                      -- upcoming
          )
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEventsAvailable(String search, Integer page, Integer size);

    @Select("""
        SELECT count(*) FROM events WHERE title ILIKE CONCAT('%', #{search}, '%')
        AND (
            (start_date <= NOW() AND end_date >= NOW())  -- ongoing
            OR (start_date > NOW())                      -- upcoming
          )
    """)
    Integer countAllEventAvailable(String search);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        AND (
            (start_date <= NOW() AND end_date >= NOW())  -- ongoing
            OR (start_date > NOW())                      -- upcoming
          )
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEventsWithDateFilterAvailable(String search, Integer page, Integer size, LocalDate dateFilter);

    @Select("""
        SELECT count(*) FROM events WHERE title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        AND (
            (start_date <= NOW() AND end_date >= NOW())  -- ongoing
            OR (start_date > NOW())                      -- upcoming
          )
    """)
    Integer countAllEventWithFilterAvailable(String search, LocalDate dateFilter);

    // NEW: AVAILABLE EVENTS BY MUSEUM ID
    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events
        WHERE museum_id = #{museumId}::UUID
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND (
            (start_date <= NOW() AND end_date >= NOW())  -- ongoing
            OR (start_date > NOW())                      -- upcoming
          )
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEventsAvailableByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("page") Integer page, @Param("size") Integer size);

    @Select("""
        SELECT count(*) FROM events WHERE museum_id = #{museumId}::UUID
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND (
            (start_date <= NOW() AND end_date >= NOW())  -- ongoing
            OR (start_date > NOW())                      -- upcoming
          )
    """)
    Integer countAllEventAvailableByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events
        WHERE museum_id = #{museumId}::UUID
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        AND (
            (start_date <= NOW() AND end_date >= NOW())  -- ongoing
            OR (start_date > NOW())                      -- upcoming
          )
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEventsWithDateFilterAvailableByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("page") Integer page, @Param("size") Integer size, @Param("dateFilter") LocalDate dateFilter);

    @Select("""
        SELECT count(*) FROM events WHERE museum_id = #{museumId}::UUID 
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        AND (
            (start_date <= NOW() AND end_date >= NOW())  -- ongoing
            OR (start_date > NOW())                      -- upcoming
          )
    """)
    Integer countAllEventWithFilterAvailableByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("dateFilter") LocalDate dateFilter);

    // ONGOING EVENTS
    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events 
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND start_date <= NOW() 
        AND end_date >= NOW()
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size} 
        LIMIT #{size}
    """)
    List<Event> findAllEventsOnGoing(String search, Integer page, Integer size);

    @Select("""
        SELECT count(*) FROM events 
        WHERE title ILIKE CONCAT('%', #{search}, '%')
        AND is_deleted = false
        AND start_date <= NOW() 
        AND end_date >= NOW()
    """)
    Integer countAllEventOnGoing(String search);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND #{dateFilter} BETWEEN DATE(start_date) AND DATE(end_date)
        AND start_date <= NOW()
        AND end_date >= NOW()
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size} 
        LIMIT #{size}
    """)
    List<Event> findAllEventsWithDateFilterOngoing(String search, Integer page, Integer size, LocalDate dateFilter);

    @Select("""
        SELECT count(*) FROM events 
        WHERE title ILIKE CONCAT('%', #{search}, '%')
        AND is_deleted = false
        AND #{dateFilter} BETWEEN DATE(start_date) AND DATE(end_date)
        AND start_date <= NOW() 
        AND end_date >= NOW()
    """)
    Integer countAllEventWithFilterOnGoing(String search, LocalDate dateFilter);

    // NEW: ONGOING EVENTS BY MUSEUM ID
    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events
        WHERE museum_id = #{museumId}::UUID
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND start_date <= NOW() 
        AND end_date >= NOW()
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size} 
        LIMIT #{size}
    """)
    List<Event> findAllEventsOnGoingByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("page") Integer page, @Param("size") Integer size);

    @Select("""
        SELECT count(*) FROM events WHERE museum_id = #{museumId}::UUID 
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND start_date <= NOW() 
        AND end_date >= NOW()
    """)
    Integer countAllEventOnGoingByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events 
        WHERE museum_id = #{museumId}::UUID 
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND #{dateFilter} BETWEEN DATE(start_date) AND DATE(end_date)                               
        AND start_date <= NOW() 
        AND end_date >= NOW()
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size} 
        LIMIT #{size}
    """)
    List<Event> findAllEventsWithDateFilterOngoingByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("page") Integer page, @Param("size") Integer size, @Param("dateFilter") LocalDate dateFilter);

    @Select("""
        SELECT count(*) FROM events WHERE museum_id = #{museumId}::UUID 
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND #{dateFilter} BETWEEN DATE(start_date) AND DATE(end_date)
        AND start_date <= NOW() 
        AND end_date >= NOW()
    """)
    Integer countAllEventWithFilterOnGoingByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("dateFilter") LocalDate dateFilter);

    // UPCOMING EVENTS
    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events 
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND start_date > now()
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size} 
        LIMIT #{size}
    """)
    List<Event> findAllEventsUpComing(String search, Integer page, Integer size);

    @Select("""
        SELECT count(*) FROM events 
        WHERE title ILIKE CONCAT('%', #{search}, '%')
        AND is_deleted = false
        AND start_date > now()
    """)
    Integer countAllEventUpComing(String search);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events 
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND start_date::date = #{dateFilter}
        AND start_date > now()
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size} 
        LIMIT #{size}
    """)
    List<Event> findAllEventsWithDateFilterUpComing(String search, Integer page, Integer size, LocalDate dateFilter);

    @Select("""
        SELECT count(*) FROM events 
        WHERE title ILIKE CONCAT('%', #{search}, '%')
        AND is_deleted = false
        AND start_date::date = #{dateFilter}
        AND start_date > now()
    """)
    Integer countAllEventWithFilterUpComing(String search, LocalDate dateFilter);

    // NEW: UPCOMING EVENTS BY MUSEUM ID
    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events 
        WHERE museum_id = #{museumId}::UUID 
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND start_date > now()
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size} 
        LIMIT #{size}
    """)
    List<Event> findAllEventsUpComingByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("page") Integer page, @Param("size") Integer size);

    @Select("""
        SELECT count(*) FROM events WHERE museum_id = #{museumId}::UUID 
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND start_date > now()
    """)
    Integer countAllEventUpComingByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events 
        WHERE museum_id = #{museumId}::UUID 
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND start_date::date = #{dateFilter}
        AND start_date > now()
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size} 
        LIMIT #{size}
    """)
    List<Event> findAllEventsWithDateFilterUpComingByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("page") Integer page, @Param("size") Integer size, @Param("dateFilter") LocalDate dateFilter);

    @Select("""
        SELECT count(*) FROM events WHERE museum_id = #{museumId}::UUID 
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND start_date::date = #{dateFilter}
        AND start_date > now()
    """)
    Integer countAllEventWithFilterUpComingByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("dateFilter") LocalDate dateFilter);

    // ENDED EVENTS
    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND end_date < now()
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEventsEnded(String search, Integer page, Integer size);

    @Select("""
        SELECT count(*) FROM events 
        WHERE title ILIKE CONCAT('%', #{search}, '%')
        AND is_deleted = false
        AND end_date < now()
    """)
    Integer countAllEventEnded(String search);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events 
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        AND end_date < now()
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size} 
        LIMIT #{size}
    """)
    List<Event> findAllEventsWithDateFilterEnded(String search, Integer page, Integer size, LocalDate dateFilter);

    @Select("""
        SELECT count(*) FROM events 
        WHERE title ILIKE CONCAT('%', #{search}, '%')
        AND is_deleted = false
        AND DATE(start_date) = #{dateFilter}
        AND end_date < now()
    """)
    Integer countAllEventWithFilterEnded(String search, LocalDate dateFilter);

    // NEW: ENDED EVENTS BY MUSEUM ID
    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events
        WHERE museum_id = #{museumId}::UUID 
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND end_date < now()
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEventsEndedByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("page") Integer page, @Param("size") Integer size);

    @Select("""
        SELECT count(*) FROM events WHERE museum_id = #{museumId}::UUID 
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND end_date < now()
    """)
    Integer countAllEventEndedByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events 
        WHERE museum_id = #{museumId}::UUID 
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        AND end_date < now()
        ORDER BY start_date
        OFFSET (#{page} - 1) * #{size} 
        LIMIT #{size}
    """)
    List<Event> findAllEventsWithDateFilterEndedByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("page") Integer page, @Param("size") Integer size, @Param("dateFilter") LocalDate dateFilter);

    @Select("""
        SELECT count(*) FROM events WHERE museum_id = #{museumId}::UUID 
        AND is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        AND end_date < now()
    """)
    Integer countAllEventWithFilterEndedByMuseumId(@Param("museumId") UUID museumId, @Param("search") String search, @Param("dateFilter") LocalDate dateFilter);

    // Latest Event
    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events 
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND (
            start_date > NOW()
            OR 
            (start_date <= NOW() 
             AND start_date >= NOW() - INTERVAL '7 days'
             AND (end_date >= NOW() OR end_date IS NULL))
        )
        AND (end_date > NOW() + INTERVAL '7 days' OR end_date IS NULL)
        ORDER BY start_date ASC
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEventsLatest(@Param("search") String search, @Param("page") Integer page, @Param("size") Integer size);

    @Select("""
        SELECT count(*) FROM events 
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND (
            start_date > NOW()
            OR 
            (start_date <= NOW() 
             AND start_date >= NOW() - INTERVAL '7 days'
             AND (end_date >= NOW() OR end_date IS NULL))
        )
        AND (end_date > NOW() + INTERVAL '7 days' OR end_date IS NULL)
    """)
    Integer countAllEventLatest(@Param("search") String search);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events 
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        AND (
            start_date > NOW()
            OR 
            (start_date <= NOW() 
             AND start_date >= NOW() - INTERVAL '7 days'
             AND (end_date >= NOW() OR end_date IS NULL))
        )
        AND (end_date > NOW() + INTERVAL '7 days' OR end_date IS NULL)
        ORDER BY start_date ASC
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEventsWithDateFilterLatest(@Param("search") String search, @Param("page") Integer page, @Param("size") Integer size, @Param("dateFilter") LocalDate dateFilter);

    @Select("""
        SELECT count(*) FROM events 
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        AND (
            start_date > NOW()
            OR 
            (start_date <= NOW() 
             AND start_date >= NOW() - INTERVAL '7 days'
             AND (end_date >= NOW() OR end_date IS NULL))
        )
        AND (end_date > NOW() + INTERVAL '7 days' OR end_date IS NULL)
    """)
    Integer countAllEventWithFilterLatest(@Param("search") String search, @Param("dateFilter") LocalDate dateFilter);

    // Nearly Expired Events
    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events 
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND start_date <= NOW()
        AND end_date >= NOW()
        AND end_date <= NOW() + INTERVAL '3 days'
        ORDER BY end_date ASC
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEventsNearlyExpired(@Param("search") String search, @Param("page") Integer page, @Param("size") Integer size);

    @Select("""
        SELECT count(*) FROM events 
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND start_date <= NOW()
        AND end_date >= NOW()
        AND end_date <= NOW() + INTERVAL '3 days'
    """)
    Integer countAllEventNearlyExpired(@Param("search") String search);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events 
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        AND start_date <= NOW()
        AND end_date >= NOW()
        AND end_date <= NOW() + INTERVAL '3 days'
        ORDER BY end_date ASC
        OFFSET (#{page} - 1) * #{size}
        LIMIT #{size}
    """)
    List<Event> findAllEventsWithDateFilterNearlyExpired(@Param("search") String search, @Param("page") Integer page, @Param("size") Integer size, @Param("dateFilter") LocalDate dateFilter);

    @Select("""
        SELECT count(*) FROM events 
        WHERE is_deleted = false
        AND title ILIKE CONCAT('%', #{search}, '%')
        AND DATE(start_date) = #{dateFilter}
        AND start_date <= NOW()
        AND end_date >= NOW()
        AND end_date <= NOW() + INTERVAL '3 days'
    """)
    Integer countAllEventWithFilterNearlyExpired(@Param("search") String search, @Param("dateFilter") LocalDate dateFilter);
}
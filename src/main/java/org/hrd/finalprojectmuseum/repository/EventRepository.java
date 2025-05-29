package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.dto.request.EventRequest;
import org.hrd.finalprojectmuseum.model.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface EventRepository {

    @Select("""
        SELECT count(*) FROM events
    """)
    Integer countAllEvent();

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
        SELECT * FROM events WHERE is_deleted = false
                             AND title LIKE CONCAT('%', #{search}, '%')
                             OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<Event> findAllEvents(String search, Integer page, Integer size);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events WHERE museum_id = #{museumId}::UUID AND is_deleted = false
                             AND title LIKE CONCAT('%', #{search}, '%')
                             offset (#{page}-1)* #{size} limit #{size};
    """)
    List<Event> findAllEventsByMuseumId(String search, UUID museumId, Integer page, Integer size);


    @Select("""
        SELECT count(*) FROM events WHERE museum_id = #{museumId}::UUID
        AND is_deleted = false AND title LIKE CONCAT('%', #{search}, '%');
    """)
    Integer countAllEventByMuseumId(String search, UUID museumId);

    @ResultMap("eventMapper")
    @Select("""
        SELECT * FROM events WHERE event_id = #{eventId}::UUID;
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
        WHERE event_id = #{eventId}::UUID AND is_deleted = false RETURNING *;
    """)
    Event updateEventByEventId(UUID eventId, @Param("event") EventRequest eventRequest, LocalDateTime updatedAt);

    @Update("""
        UPDATE events SET is_deleted = true, updated_at = #{updatedAt} WHERE event_id = #{eventId}::UUID AND is_deleted = false;
    """)
    void updateDeleteStatus(UUID eventId, LocalDateTime updatedAt);
}

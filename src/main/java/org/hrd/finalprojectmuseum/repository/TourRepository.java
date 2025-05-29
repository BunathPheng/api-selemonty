package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.entity.Tour;
import org.hrd.finalprojectmuseum.model.entity.TourGuide;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface TourRepository {

    @Results(id = "tourMapper", value = {
            @Result(property = "tourId", column = "tour_id"),
            @Result(property = "guides", column = "tour_id",
                    many = @Many(select = "getAllTourGuids")
            ),
            @Result(property = "visitor", column = "visitor_id",
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.ProfileRepository.findVisitorById")
            ),
            @Result(property = "slotAmount", column = "slot_amount"),
            @Result(property = "bookingDate", column = "booking_date"),
            @Result(property = "expiryDate", column = "expiry_date"),
            @Result(property = "ticketStatus", column = "ticket_status"),
            @Result(property = "tourPrice", column = "tour_price"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
    })
    @Select("""
        SELECT t.tour_id, b.slot_amount, b.booking_date, b.expired_date, b.ticket_status,
        t.tour_price, b.visitor_id, t.status, t.created_at, t.updated_at
        FROM tours t
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        WHERE b.museum_id = #{museumId}::UUID AND t.tour_id = #{tourId}::UUID
    """)
    Tour getTourByTourId(UUID museumId, UUID tourId);

    @ResultMap("tourMapper")
    @Select("""
        SELECT t.tour_id, b.slot_amount, b.booking_date, b.expired_date, b.ticket_status,
        t.tour_price, b.visitor_id, t.status, t.created_at, t.updated_at
        FROM tours t
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        WHERE b.visitor_id = #{visitorId}::UUID AND t.tour_id = #{tourId}::UUID
    """)
    Tour getTourByTourIdWithVisitorId(UUID visitorId, UUID tourId);

    @Result(property = "guide", column = "guide_id",
            one = @One(select = "org.hrd.finalprojectmuseum.repository.GuideRepository.findGuideByGuideId")
    )
    @Select("""
        SELECT guide_id FROM tour_guides WHERE tour_id = #{tourId}::UUID
    """)
    List<TourGuide> getAllTourGuids(UUID tourId);

    @Insert("""
        INSERT INTO tours(booking_id, status) VALUES (#{bookingId}::UUID, 'REQUEST')
    """)
    void insertNewTourRequest(UUID bookingId);

    @Update("""
        UPDATE tours SET tour_price = #{tourPrice}, status = 'PENDING'
        WHERE tour_id = #{tourId}::UUID AND status = 'REQUEST';
    """)
    void setTourPrice(UUID tourId, BigDecimal tourPrice);

    @Select("""
        SELECT * FROM tours WHERE tour_id = #{tourId}::UUID
    """)
    Tour findTourByTourId(UUID tourId);

    @Insert("""
        INSERT INTO tour_guides(tour_id, guide_id) VALUES (#{tourId}::UUID, #{guideId}::UUID)
    """)
    void setTourGuys(UUID tourId, UUID guideId);

    @ResultMap("tourMapper")
    @Select("""
        SELECT t.tour_id, b.slot_amount, b.booking_date, b.expired_date, b.ticket_status,
        t.tour_price, b.visitor_id, t.status, t.created_at, t.updated_at
        FROM tours t
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.museum_id = #{museumId}::UUID
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<Tour> findAllTourByMuseumId(UUID museumId, String search, Integer page, Integer size);

    @ResultMap("tourMapper")
    @Select("""
        SELECT t.tour_id, b.slot_amount, b.booking_date, b.expired_date, b.ticket_status,
        t.tour_price, b.visitor_id, t.status, t.created_at, t.updated_at
        FROM tours t
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.museum_id = #{museumId}::UUID
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        AND t.status = #{status}
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<Tour> findAllTourByMuseumIdWithStatus(UUID museumId, String search, Integer page, Integer size, String status);

    @Select("""
        SELECT COUNT(t.*)
        FROM tours t
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.museum_id = #{museumId}::UUID
        AND m.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllTour(UUID museumId, String search);

    @Select("""
        SELECT COUNT(t.*)
        FROM tours t
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.museum_id = #{museumId}::UUID
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        AND t.status = #{status}
    """)
    Integer countAllTourWithStatus(UUID museumId, String search, String status);

    @ResultMap("tourMapper")
    @Select("""
        SELECT t.tour_id, b.slot_amount, b.booking_date, b.expired_date, b.ticket_status,
        t.tour_price, b.visitor_id, t.status, t.created_at, t.updated_at
        FROM tours t
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.visitor_id = #{visitorId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<Tour> findAllTourByVisitorId(UUID visitorId, String search, Integer page, Integer size);

    @Select("""
        SELECT COUNT(t.*)
        FROM tours t
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.visitor_id = #{visitorId}::UUID
        AND m.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllTourByVisitorId(UUID visitorId, String search);

    @ResultMap("tourMapper")
    @Select("""
        SELECT t.tour_id, b.slot_amount, b.booking_date, b.expired_date, b.ticket_status,
        t.tour_price, b.visitor_id, t.status, t.created_at, t.updated_at
        FROM tours t
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.visitor_id = #{visitorId}::UUID
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        AND t.status = #{status}
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<Tour> findAllTourByVisitorIdWithStatus(UUID visitorId, String search, Integer page, Integer size, String status);

    @Select("""
        SELECT COUNT(t.*)
        FROM tours t
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.visitor_id = #{visitorId}::UUID
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        AND t.status = #{status}
    """)
    Integer countAllTourWithStatusByVisitorId(UUID visitorId, String search, String status);

    @Update("""
        UPDATE tours SET status = #{status}, updated_at = #{updatedAt} WHERE tour_id = #{tourId}::UUID RETURNING booking_id;
    """)
    UUID modifyTourStatus(UUID tourId, String status, LocalDateTime updatedAt);

    @Select("""
        SELECT tour_price FROM tours WHERE booking_id = #{bookingId}::UUID
    """)
    BigDecimal getTourPriceByBookingId(UUID bookingId);
}
package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.response.TourVisitorResponse;
import org.hrd.finalprojectmuseum.model.entity.Guide;
import org.hrd.finalprojectmuseum.model.entity.Tour;
import org.hrd.finalprojectmuseum.model.entity.TourGuide;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.enums.TourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface TourRepository {

    @Results(id = "TourVisitorMapper", value = {
            @Result(property = "tourId", column = "tour_id"),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "tourPrice", column = "tour_price"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
    })
    @Select("""
        SELECT t.tour_id, t.tour_price, v.full_name, t.status, t.created_at, t.updated_at FROM tours t INNER JOIN tour_guides tg ON t.tour_id = tg.tour_id
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<TourVisitorResponse> findAllTourByMuseumId(UUID museumId, String search, Integer page, Integer size);

    @ResultMap("TourVisitorMapper")
    @Select("""
        SELECT t.tour_id, t.tour_price, v.full_name, t.status, t.created_at, t.updated_at
        FROM tours t INNER JOIN tour_guides tg ON t.tour_id = tg.tour_id
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
        AND status = #{status}
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<TourVisitorResponse> findAllTourByMuseumIdWithStatus(UUID museumId, String search, Integer page, Integer size, String status);

    @Select("""
        SELECT COUNT(*) FROM tours t INNER JOIN tour_guides tg ON t.tour_id = tg.tour_id
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%');
    """)
    Integer countAllTour(UUID museumId, String search);

    @Select("""
        SELECT COUNT(*) FROM tours t INNER JOIN tour_guides tg ON t.tour_id = tg.tour_id
        INNER JOIN bookings b ON t.booking_id = b.booking_id
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
        AND status = #{status}
    """)
    Integer countAllTourWithStatus(UUID museumId, String search, String status);

    @Results(id = "TourVisitorDetailMapper", value = {
            @Result(property = "tourId", column = "tour_id"),
            @Result(property = "guides", column = "tour_id",
                    many = @Many(select = "getAllTourGuids")
            ),
            @Result(property = "visitor", column = "visitor_id",
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.VisitorRepository.findVisitorById")
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

    @Result(property = "guide   ", column = "guide_id",
            one = @One(select = "org.hrd.finalprojectmuseum.repository.GuideRepository.findGuideByGuideId")
    )
    @Select("""
        SELECT guide_id FROM tour_guides WHERE tour_id = #{tourId}::UUID
    """)
    List<TourGuide> getAllTourGuids(UUID tourId);
}

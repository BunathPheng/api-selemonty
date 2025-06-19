package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.entity.VisitorBooking;
import org.hrd.finalprojectmuseum.model.entity.VisitorBookingDetail;
import org.hrd.finalprojectmuseum.model.entity.VisitorBookingTotal;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface VisitorRepository {

    @Results(id = "visitorMapper", value = {
            @Result(property = "visitorId", column = "visitor_id"),
            @Result(property = "appUserRegister", column = "user_id",
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.AppUserRepository.getUserById")),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "contactNumber", column = "contact_number"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "dob", column = "dob"),
            @Result(property = "profileImageLink", column = "profile_image_link"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "bookingCount", column = "booking_count") // Add booking count
    })
    @Select("""
        SELECT v.*, COUNT(b.booking_id) as booking_count
        FROM visitors v
        INNER JOIN bookings b ON v.visitor_id = b.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}::TEXT, '%')
        GROUP BY v.visitor_id, v.user_id, v.full_name, v.contact_number, v.gender, v.dob, v.profile_image_link, v.created_at, v.updated_at
        ORDER BY booking_count DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<Visitor> findVisitorByMuseumId(UUID museumId, String search, Integer page, Integer size);

    @Select("""
        SELECT COUNT(DISTINCT v.visitor_id)
        FROM visitors v
        INNER JOIN bookings b ON v.visitor_id = b.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}::TEXT, '%')
    """)
    Integer countAllVisitorByMuseumId(UUID museumId, String search);

    @ResultMap("visitorMapper")
    @Select("""
        SELECT * FROM visitors
        WHERE full_name ILIKE CONCAT('%', #{search}, '%')
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<Visitor> findAllVisitor(String search, Integer page, Integer size);

    @ResultMap("visitorMapper")
    @Select("""
        SELECT * FROM visitors WHERE visitor_id = #{visitorId}::UUID;
    """)
    Visitor findVisitorById(UUID visitorId);

    @Select("""
        SELECT COUNT(*) FROM visitors WHERE full_name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllVisitor(String search);

    @Results(id = "visitorBookingMapper", value = {
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "email", column = "email"),
            @Result(property = "totalBookings", column = "total_booking"),
            @Result(property = "totalTickets", column = "total_ticket")
    })
    @Select("""
        SELECT
            v.full_name,
            u.email,
            COUNT(b.booking_id) as total_booking,
            COALESCE(SUM(b.slot_amount), 0) as total_ticket
        FROM visitors v
        LEFT JOIN bookings b ON v.visitor_id = b.visitor_id
        INNER JOIN user_info u ON v.user_id = u.user_id
        LEFT JOIN tours t ON t.booking_id = b.booking_id
        WHERE ((t.booking_id IS NULL) OR (t.booking_id IS NOT NULL AND t.status = 'PAID'))
        AND (#{search} IS NULL OR v.full_name ILIKE CONCAT('%', #{search}, '%'))
        GROUP BY v.full_name, u.email
        ORDER BY total_ticket DESC
        OFFSET (#{page}-1) * #{size} LIMIT #{size}
    """)
    List<VisitorBooking> findAllVisitorBooking(@Param("search") String search, @Param("page") Integer page, @Param("size") Integer size);

    @Select("""
        SELECT
            COUNT(b.booking_id)
        FROM visitors v
        LEFT JOIN bookings b ON v.visitor_id = b.visitor_id
        INNER JOIN user_info u ON v.user_id = u.user_id
        LEFT JOIN tours t ON t.booking_id = b.booking_id
        WHERE (t.booking_id IS NULL) OR (t.booking_id IS NOT NULL AND t.status = 'PAID')
        AND v.full_name ILIKE CONCAT('%', #{search}, '%') OR u.email ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countVisitorBooking(String search);

    @Results(id = "visitorBookingDetailMapper", value = {
            @Result(property = "museumName", column = "name"),
            @Result(property = "ticketType", column = "ticket_type"),
            @Result(property = "bookingType", column = "booking_type"),
            @Result(property = "bookingDate", column = "booking_date")
    })
    @Select("""
        SELECT m.name, b.ticket_type, b.booking_type, b.booking_date
        FROM bookings b INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        LEFT JOIN tours t ON b.booking_id = t.booking_id
        WHERE visitor_id = #{visitorId}::UUID AND m.name ILIKE CONCAT('%', #{search}, '%')
        AND (t.booking_id IS NULL OR (t.tour_id IS NOT NULL AND t.status = 'PAID'))
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<VisitorBookingDetail> findVisitorBookingByVisitorId(UUID visitorId, String search, Integer page, Integer size);

    @Select("""
        SELECT COUNT(b.booking_id) FROM bookings b INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        LEFT JOIN tours t ON b.booking_id = t.booking_id
        WHERE visitor_id = #{visitorId}::UUID AND m.name ILIKE CONCAT('%', #{search}, '%')
        AND (t.booking_id IS NULL OR (t.tour_id IS NOT NULL AND t.status = 'PAID'))
    """)
    Integer countBookingByVisitorId(UUID visitorId, String search);

    @Results(id = "visitorBookingBookingMapper", value = {
            @Result(property = "totalTicket", column = "total_ticket"),
            @Result(property = "totalBooking", column = "total_booking"),
    })
    @Select("""
        SELECT
            COUNT(b.booking_id) as total_booking,
            COALESCE(SUM(b.slot_amount), 0) as total_ticket
        FROM visitors v
        LEFT JOIN bookings b ON v.visitor_id = b.visitor_id
        INNER JOIN user_info u ON v.user_id = u.user_id
        LEFT JOIN tours t ON t.booking_id = b.booking_id
        WHERE ((t.booking_id IS NULL) OR (t.booking_id IS NOT NULL AND t.status = 'PAID'))
        AND v.visitor_id = #{visitorId}::UUID AND u.is_verified = true
    """)
    VisitorBookingTotal retrieveBookingTotalByVisitorId(UUID visitorId);

    @ResultMap("visitorBookingBookingMapper")
    @Select("""
        SELECT
            COUNT(b.booking_id) as total_booking,
            COALESCE(SUM(b.slot_amount), 0) as total_ticket
        FROM visitors v
        LEFT JOIN bookings b ON v.visitor_id = b.visitor_id
        INNER JOIN user_info u ON v.user_id = u.user_id
        LEFT JOIN tours t ON t.booking_id = b.booking_id
        WHERE ((t.booking_id IS NULL) OR (t.booking_id IS NOT NULL AND t.status = 'PAID'))
        AND v.visitor_id = #{visitorId}::UUID  AND u.is_verified = true
        AND b.created_at >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
        AND b.created_at <= (CURRENT_DATE - INTERVAL '1 month') + (CURRENT_DATE - DATE_TRUNC('month', CURRENT_DATE))
    """)
    VisitorBookingTotal retrieveLastMonthBookingTotalByVisitorId(UUID visitorId);

    @ResultMap("visitorBookingBookingMapper")
    @Select("""
        SELECT
            COUNT(b.booking_id) as total_booking,
            COALESCE(SUM(b.slot_amount), 0) as total_ticket
        FROM visitors v
        LEFT JOIN bookings b ON v.visitor_id = b.visitor_id
        INNER JOIN user_info u ON v.user_id = u.user_id
        LEFT JOIN tours t ON t.booking_id = b.booking_id
        WHERE ((t.booking_id IS NULL) OR (t.booking_id IS NOT NULL AND t.status = 'PAID'))
        AND v.visitor_id = #{visitorId}::UUID AND u.is_verified = true
        AND b.created_at >= DATE_TRUNC('month', CURRENT_DATE)
        AND b.created_at <= CURRENT_DATE
    """)
    VisitorBookingTotal retrieveThisMonthBookingTotalByVisitorId(@Param("visitorId") UUID visitorId);

    @Select("""
        SELECT COUNT(visitor_id) FROM visitors v
        INNER JOIN user_info u ON v.user_id = u.user_id
        WHERE v.created_at >= #{startDate} AND v.created_at <= #{endDate}
        AND u.is_verified = true
    """)
    Integer countNewVisitorsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Select("""
        SELECT COUNT(visitor_id) FROM visitors v
        INNER JOIN user_info u ON v.user_id = u.user_id
        WHERE v.created_at < #{endDate} AND u.is_verified = true
    """)
    Integer countTotalVisitors(LocalDateTime endDate);

    @ResultMap("visitorMapper")
    @Select("""
        SELECT v.*
        FROM visitors v
        INNER JOIN bookings b ON v.visitor_id = b.visitor_id
        LEFT JOIN tours t ON t.booking_id = b.booking_id
        WHERE b.museum_id = #{museumId}::UUID
        AND (t.tour_id IS NULL OR (t.tour_id IS NOT NULL AND t.status = 'PAID'))
        GROUP BY v.visitor_id, v.user_id, v.full_name, v.contact_number,
                 v.gender, v.dob, v.profile_image_link, v.created_at, v.updated_at
        ORDER BY SUM(b.slot_amount) DESC
        LIMIT 5
    """)
    List<Visitor> findTopVisitorByMuseumId(UUID museumId);

    @Select("""
        SELECT COUNT(DISTINCT visitor_id) 
        FROM bookings 
        WHERE museum_id = #{museumId}::UUID
        AND booking_date <= #{endDate}
        AND ticket_status = 'VALID'
    """)
    Integer countVisitorByMuseumIdAndEndDate(UUID museumId, LocalDateTime endDate);

    @Select("""
        SELECT COUNT(DISTINCT visitor_id) 
        FROM bookings 
        WHERE museum_id = #{museumId}::UUID
        AND ticket_status = 'VALID'
        AND booking_date BETWEEN #{startDate} AND #{endDate}
    """)
    Integer countVisitorByMuseumIdAndDateRange(UUID museumId, LocalDateTime startDate, LocalDateTime endDate);

    @Select("""
        SELECT COUNT(DISTINCT b.visitor_id) 
        FROM bookings b
        LEFT JOIN (
            SELECT DISTINCT visitor_id
            FROM bookings
            WHERE museum_id = #{museumId}::UUID
            AND booking_date < #{startDate}
            AND ticket_status = 'VALID'
        ) prior ON b.visitor_id = prior.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND b.booking_date BETWEEN #{startDate} AND #{endDate}
        AND b.ticket_status = 'VALID'
        AND prior.visitor_id IS NULL
    """)
    Integer countNewVisitorByMuseumIdAndDateRange(UUID museumId, LocalDateTime startDate, LocalDateTime endDate);
}

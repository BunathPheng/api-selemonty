package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.dto.request.BookingRequest;
import org.hrd.finalprojectmuseum.model.dto.response.BookingDetail;
import org.hrd.finalprojectmuseum.model.dto.response.BookingManagement;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.enums.BookingType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface BookingRepository {

    @Results(id = "bookingMapper", value = {
            @Result(property = "bookingId", column = "booking_id"),
            @Result(property = "museum", column = "museum_id", javaType = UUID.class, jdbcType = JdbcType.VARCHAR,
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.MuseumRepository.findMuseumByMuseumId")
            ),
            @Result(property = "visitorId", column = "visitor_id"),
            @Result(property = "ticketPrice", column = "ticket_price"),
            @Result(property = "ticketType", column = "ticket_type"),
            @Result(property = "ticketStatus", column = "ticket_status"),
            @Result(property = "bookingType", column = "booking_type"),
            @Result(property = "slotAmount", column = "slot_amount"),
            @Result(property = "bookingDate", column = "booking_date"),
            @Result(property = "expiryDate", column = "expired_date"),
            @Result(property = "qrCode", column = "qr_code"),
            @Result(property = "totalPrice", column = "total_price"),
            @Result(property = "createAt", column = "created_at")
    })
    @Select("""
    SELECT booking_id, museum_id, visitor_id, ticket_price, ticket_type, 
           ticket_status, booking_type, slot_amount, booking_date, 
           expired_date, qr_code, total_price, created_at
    FROM bookings 
    WHERE booking_id = #{bookingId}::UUID
""")
    Booking findById(UUID bookingId);

    @ResultMap("bookingMapper")
    @Select("""
    INSERT INTO bookings (
        museum_id, visitor_id, ticket_price, ticket_type,
        ticket_status, booking_type, slot_amount, booking_date,
        expired_date, qr_code, total_price
    )
    VALUES (
        #{museumId}::UUID, #{visitorId}::UUID,
        #{bookingRequest.ticketPrice}, #{bookingRequest.ticketType},
        default, #{bookingRequest.bookingType},
        #{bookingRequest.slotAmount}, #{bookingRequest.bookingDate},
        #{expiredDate}, #{code},
        #{bookingRequest.totalPrice}
    )
    RETURNING *;
""")
    Booking insertBookingByMuseumId(UUID museumId, UUID visitorId, String code, LocalDateTime expiredDate, @Param("bookingRequest") BookingRequest bookingRequest);


    @Select("""
        SELECT EXISTS(SELECT 1 FROM bookings WHERE qr_code = #{code})
    """)
    Boolean existsByTextCode(String code);

    @ResultMap("bookingMapper")
    @Select("SELECT * FROM bookings WHERE visitor_id = #{visitorId}::UUID")
    List<Booking> findAllByVisitorId(UUID visitorId);

    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b 
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
    """)
    List<Booking> findByVisitorIdAndSearch(UUID visitorId, String search);

    // Search by category only
    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.visitor_id = #{visitorId}::UUID
        AND b.booking_type = #{bookingType}
    """)
    List<Booking> findByVisitorIdAndCategory(UUID visitorId, BookingType bookingType);

    // Search by both museum name and category
    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
        AND b.booking_type = #{bookingType}
    """)
    List<Booking> findByVisitorIdSearchAndCategory(UUID visitorId, String search, BookingType bookingType);

    // Search by booking type only
    @ResultMap("bookingMapper")
        @Select("""
        SELECT * FROM bookings 
        WHERE visitor_id = #{visitorId}::UUID
        AND booking_type = #{bookingType}
    """)
    List<Booking> findByVisitorIdAndBookingType(UUID visitorId, BookingType bookingType);

    // Search by museum name and booking type
    @ResultMap("bookingMapper")
        @Select("""
        SELECT b.* FROM bookings b
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
        AND b.booking_type = #{bookingType}
    """)
    List<Booking> findByVisitorIdSearchAndBookingType(UUID visitorId, String search, BookingType bookingType);

    // Date range only
    @ResultMap("bookingMapper")
    @Select("""
    SELECT * FROM bookings 
    WHERE visitor_id = #{visitorId}::UUID
    AND created_at >= #{startDate}
    AND created_at <= #{endDate}
""")
    List<Booking> findByVisitorIdAndDateRange(UUID visitorId, LocalDate startDate, LocalDate endDate);

    // Search + Date range
    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b 
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
        AND b.created_at >= #{startDate}
        AND b.created_at <= #{endDate}
    """)
    List<Booking> findByVisitorIdSearchAndDateRange(UUID visitorId, String search, LocalDate startDate, LocalDate endDate);

    // Booking type + Date range
    @ResultMap("bookingMapper")
    @Select("""
        SELECT * FROM bookings 
        WHERE visitor_id = #{visitorId}::UUID
        AND booking_type = #{bookingType}
        AND created_at >= #{startDate}
        AND created_at <= #{endDate}
    """)
    List<Booking> findByVisitorIdBookingTypeAndDateRange(UUID visitorId, BookingType bookingType, LocalDate startDate, LocalDate endDate);

    // All filters
    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
        AND b.booking_type = #{bookingType}
        AND b.created_at >= #{startDate}
        AND b.created_at <= #{endDate}
    """)
    List<Booking> findByVisitorIdSearchBookingTypeAndDateRange(UUID visitorId, String search, BookingType bookingType, LocalDate startDate, LocalDate endDate);

    @Results(id = "bookingManagementMapper", value = {
            @Result(property = "bookingId", column = "booking_id",
                    javaType = UUID.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "visitor", column = "visitor_id",
                one = @One(select = "org.hrd.finalprojectmuseum.repository.VisitorRepository.findVisitorById")
            ),
            @Result(property = "ticketPrice", column = "ticket_price"),
            @Result(property = "ticketType", column = "ticket_type"),
            @Result(property = "ticketStatus", column = "ticket_status"),
            @Result(property = "bookingType", column = "booking_type"),
            @Result(property = "slotAmount", column = "slot_amount"),
            @Result(property = "bookingDate", column = "booking_date"),
            @Result(property = "expiryDate", column = "expired_date"),
            @Result(property = "qrCode", column = "qr_code"),
            @Result(property = "totalPrice", column = "total_price"),
            @Result(property = "createAt", column = "created_at")
    })
    @Select("""
        SELECT * FROM bookings WHERE museum_id = #{museumId}::UUID
        OFFSET (#{page}-1)* #{size} LIMIT #{size}
    """)
    List<BookingManagement> findAllBookingByMuseumId(UUID museumId, Integer page, Integer size);

    @ResultMap("bookingManagementMapper")
    @Select("""
        SELECT b.* FROM bookings b INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<BookingManagement> findAllBookingByMuseumIdAndSearch(UUID museumId, String search, Integer page, Integer size);

    @Select("""
        SELECT COUNT(b.*) FROM bookings b INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer findTotalBooking(UUID museumId, String search);

    @Results(id = "bookingDetailMapper", value = {
            @Result(property = "bookingId", column = "booking_id",
                    javaType = UUID.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "museumName", column = "name"),
            @Result(property = "ticketPrice", column = "ticket_price"),
            @Result(property = "ticketType", column = "ticket_type"),
            @Result(property = "ticketStatus", column = "ticket_status"),
            @Result(property = "bookingType", column = "booking_type"),
            @Result(property = "slotAmount", column = "slot_amount"),
            @Result(property = "bookingDate", column = "booking_date"),
            @Result(property = "expiryDate", column = "expired_date"),
            @Result(property = "qrCode", column = "qr_code"),
            @Result(property = "totalPrice", column = "total_price"),
            @Result(property = "createAt", column = "created_at")
    })
    @Select("""
        SELECT b.booking_id, m.name, b.ticket_price, b.ticket_type, b.ticket_status, b.booking_type,
        b.slot_amount, b.booking_date, b.expired_date, b.qr_code, b.total_price, b.created_at
        FROM bookings b INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.booking_id = #{bookingId}::UUID AND b.museum_id = #{museumId}::UUID;
    """)
    BookingDetail findBookingByBookingIdAndMuseumId(UUID bookingId, UUID museumId);

    @ResultMap("bookingDetailMapper")
    @Select("""
        SELECT b.booking_id, m.name, b.ticket_price, b.ticket_type, b.ticket_status, b.booking_type,
        b.slot_amount, b.booking_date, b.expired_date, b.qr_code, b.total_price, b.created_at
        FROM bookings b INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.booking_id = #{bookingId}::UUID AND b.visitor_id = #{visitor}::UUID;
    """)
    BookingDetail findBookingByBookingIdAndVisitorId(UUID bookingId, UUID visitor);

    @ResultMap("bookingDetailMapper")
    @Select("""
        SELECT b.booking_id, m.name, b.ticket_price, b.ticket_type, b.ticket_status, b.booking_type,
        b.slot_amount, b.booking_date, b.expired_date, b.qr_code, b.total_price, b.created_at
        FROM bookings b INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.booking_id = #{bookingId}::UUID;
    """)
    BookingDetail findBookingByBookingId(UUID bookingId);

    @Update("""
        UPDATE bookings SET ticket_status = #{expired} WHERE booking_id = #{bookingId}::UUID;
    """)
    void updateStatus(UUID bookingId, String expired);

    @ResultMap("bookingDetailMapper")
    @Select("""
        SELECT b.booking_id, m.name, b.ticket_price, b.ticket_type, b.ticket_status, b.booking_type,
        b.slot_amount, b.booking_date, b.expired_date, b.qr_code, b.total_price, b.created_at
        FROM bookings b INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.qr_code = #{codeQr} AND b.museum_id = #{museumId}::UUID;
    """)
    BookingDetail findBookingByCodeQrAndMuseumId(String codeQr, UUID museumId);
}

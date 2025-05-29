package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.dto.request.BookingRequest;
import org.hrd.finalprojectmuseum.model.dto.request.RequestTourRequest;
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
            @Result(property = "visitor", column = "visitor_id",
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.ProfileRepository.findVisitorById")
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
            default, 'INDIVIDUAL',
            #{bookingRequest.slotAmount}, #{bookingRequest.bookingDate},
            #{expiredDate}, #{code},
            #{bookingRequest.totalPrice}
        )
        RETURNING *;
    """)
    Booking insertBookingByMuseumId(UUID museumId, UUID visitorId, String code, LocalDateTime expiredDate, @Param("bookingRequest") BookingRequest bookingRequest);

    @Update("""
        UPDATE bookings SET ticket_status = #{expired} WHERE booking_id = #{bookingId}::UUID;
    """)
    void updateStatus(UUID bookingId, String expired);

    @Select("""
        SELECT EXISTS(SELECT 1 FROM bookings WHERE qr_code = #{code})
    """)
    Boolean existsByTextCode(String code);

    // ===== VISITOR BOOKING HISTORY (WITH PAGINATION) =====
    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b 
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
        OFFSET (#{page}-1) * #{size} LIMIT #{size}
    """)
    List<Booking> findByVisitorIdAndSearchWithPagination(UUID visitorId, String search, Integer page, Integer size);

    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
        AND b.booking_type = #{category}
        OFFSET (#{page}-1) * #{size} LIMIT #{size}
    """)
    List<Booking> findByVisitorIdSearchAndBookingTypeWithPagination(UUID visitorId, String search, BookingType category, Integer page, Integer size);

    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b 
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
        AND b.created_at >= #{startDate}
        AND b.created_at <= #{endDate}
        OFFSET (#{page}-1) * #{size} LIMIT #{size}
    """)
    List<Booking> findByVisitorIdSearchAndDateRangeWithPagination(UUID visitorId, String search, LocalDate startDate, LocalDate endDate, Integer page, Integer size);

    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
        AND b.booking_type = #{category}
        AND b.created_at >= #{startDate}
        AND b.created_at <= #{endDate}
        OFFSET (#{page}-1) * #{size} LIMIT #{size}
    """)
    List<Booking> findByVisitorIdSearchBookingTypeAndDateRangeWithPagination(UUID visitorId, String search, BookingType category, LocalDate startDate, LocalDate endDate, Integer page, Integer size);

    // ===== VISITOR BOOKING HISTORY COUNT METHODS =====
    @Select("""
        SELECT COALESCE(COUNT(b.*), 0) FROM bookings b 
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countByVisitorIdAndSearch(UUID visitorId, String search);

    @Select("""
        SELECT COALESCE(COUNT(b.*), 0) FROM bookings b
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
        AND b.booking_type = #{category}
    """)
    Integer countByVisitorIdSearchAndBookingType(UUID visitorId, String search, BookingType category);

    @Select("""
        SELECT COALESCE(COUNT(b.*), 0) FROM bookings b 
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
        AND b.created_at >= #{startDate}
        AND b.created_at <= #{endDate}
    """)
    Integer countByVisitorIdSearchAndDateRange(UUID visitorId, String search, LocalDate startDate, LocalDate endDate);

    @Select("""
        SELECT COALESCE(COUNT(b.*), 0) FROM bookings b
        INNER JOIN museum_owners mo ON b.museum_id = mo.museum_id 
        WHERE b.visitor_id = #{visitorId}::UUID
        AND mo.name ILIKE CONCAT('%', #{search}, '%')
        AND b.booking_type = #{category}
        AND b.created_at >= #{startDate}
        AND b.created_at <= #{endDate}
    """)
    Integer countByVisitorIdSearchBookingTypeAndDateRange(UUID visitorId, String search, BookingType category, LocalDate startDate, LocalDate endDate);

    // ===== MUSEUM BOOKING MANAGEMENT (WITH PAGINATION) =====
    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b 
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
        OFFSET (#{page}-1) * #{size} LIMIT #{size}
    """)
    List<Booking> findAllBookingByMuseumIdAndSearchWithPagination(UUID museumId, String search, Integer page, Integer size);

    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b 
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
        AND b.booking_type = #{bookingType}
        OFFSET (#{page}-1) * #{size} LIMIT #{size}
    """)
    List<Booking> findAllBookingByMuseumIdSearchAndBookingTypeWithPagination(UUID museumId, String search, BookingType bookingType, Integer page, Integer size);

    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b 
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
        AND b.created_at >= #{startDate}
        AND b.created_at <= #{endDate}
        OFFSET (#{page}-1) * #{size} LIMIT #{size}
    """)
    List<Booking> findAllBookingByMuseumIdSearchAndDateRangeWithPagination(UUID museumId, String search, LocalDate startDate, LocalDate endDate, Integer page, Integer size);

    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b 
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
        AND b.booking_type = #{bookingType}
        AND b.created_at >= #{startDate}
        AND b.created_at <= #{endDate}
        OFFSET (#{page}-1) * #{size} LIMIT #{size}
    """)
    List<Booking> findAllBookingByMuseumIdSearchBookingTypeAndDateRangeWithPagination(UUID museumId, String search, BookingType bookingType, LocalDate startDate, LocalDate endDate, Integer page, Integer size);

    // ===== MUSEUM BOOKING MANAGEMENT COUNT METHODS =====
    @Select("""
        SELECT COALESCE(COUNT(b.*), 0) FROM bookings b 
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllBookingByMuseumIdAndSearch(UUID museumId, String search);

    @Select("""
        SELECT COALESCE(COUNT(b.*), 0) FROM bookings b 
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
        AND b.booking_type = #{bookingType}
    """)
    Integer countAllBookingByMuseumIdSearchAndBookingType(UUID museumId, String search, BookingType bookingType);

    @Select("""
        SELECT COALESCE(COUNT(b.*), 0) FROM bookings b 
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
        AND b.created_at >= #{startDate}
        AND b.created_at <= #{endDate}
    """)
    Integer countAllBookingByMuseumIdSearchAndDateRange(UUID museumId, String search, LocalDate startDate, LocalDate endDate);

    @Select("""
        SELECT COALESCE(COUNT(b.*), 0) FROM bookings b 
        INNER JOIN visitors v ON b.visitor_id = v.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND v.full_name ILIKE CONCAT('%', #{search}, '%')
        AND b.booking_type = #{bookingType}
        AND b.created_at >= #{startDate}
        AND b.created_at <= #{endDate}
    """)
    Integer countAllBookingByMuseumIdSearchBookingTypeAndDateRange(UUID museumId, String search, BookingType bookingType, LocalDate startDate, LocalDate endDate);

    // ===== INDIVIDUAL BOOKING OPERATIONS =====
    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b 
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.booking_id = #{bookingId}::UUID AND b.museum_id = #{museumId}::UUID
    """)
    Booking findBookingByBookingIdAndMuseumId(UUID bookingId, UUID museumId);

    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b 
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.booking_id = #{bookingId}::UUID AND b.visitor_id = #{visitorId}::UUID
    """)
    Booking findBookingByBookingIdAndVisitorId(UUID bookingId, UUID visitorId);

    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b 
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.booking_id = #{bookingId}::UUID
    """)
    Booking findBookingByBookingId(UUID bookingId);

    @ResultMap("bookingMapper")
    @Select("""
        SELECT b.* FROM bookings b
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.qr_code = #{codeQr} AND b.museum_id = #{museumId}::UUID
    """)
    Booking findBookingByCodeQrAndMuseumId(String codeQr, UUID museumId);

    @Select("""
        INSERT INTO bookings (museum_id, visitor_id, slot_amount, booking_date)
        VALUES (#{museumId}, #{visitorId}, #{booking.slotAmount}, #{booking.bookingDate})
        RETURNING booking_id;
    """)
    UUID insertBookingForTourRequest(UUID museumId, UUID visitorId, @Param("booking") RequestTourRequest requestTourRequest);

    @Update("""
        UPDATE bookings SET qr_code = #{code} WHERE booking_id = #{bookingId}::UUID
    """)
    void setTicketCode(UUID bookingId, String code);
}
package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.dto.request.BookingRequest;
import org.hrd.finalprojectmuseum.model.dto.request.RequestTourRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.BookingRequestV2;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.entity.BookingAnalytics;
import org.hrd.finalprojectmuseum.model.entity.Schedule;
import org.hrd.finalprojectmuseum.model.entity.visitor.BookingV2;
import org.hrd.finalprojectmuseum.model.entity.visitor.IndividualBookingInfo;
import org.hrd.finalprojectmuseum.model.entity.visitor.MuseumSchedule;
import org.hrd.finalprojectmuseum.model.enums.BookingType;
import org.hrd.finalprojectmuseum.model.enums.TicketType;

import java.math.BigDecimal;
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
        SELECT * FROM bookings
        WHERE booking_id = #{bookingId}::UUID AND museum_id = #{museumId}::UUID
    """)
    Booking findBookingByBookingIdAndMuseumId(UUID bookingId, UUID museumId);

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
        INSERT INTO bookings (museum_id, visitor_id, booking_type, slot_amount, booking_date)
        VALUES (#{museumId}::UUID, #{visitorId}::UUID, 'TOUR', #{booking.slotAmount}, #{booking.bookingDate})
        RETURNING booking_id;
    """)
    UUID insertBookingForTourRequest(UUID museumId, UUID visitorId, @Param("booking") RequestTourRequest requestTourRequest);

    @Select("""
        UPDATE bookings SET qr_code = #{code}, expired_date = #{expiredDate}, total_price = #{tourPrice}
        WHERE booking_id = #{bookingId}::UUID
        RETURNING visitor_id
    """)
    UUID setTicketCode(UUID bookingId, String code, BigDecimal tourPrice, LocalDateTime expiredDate);

    @Select("""
        SELECT bk.booking_id, mo.name, bk.booking_type, vt.full_name, bk.ticket_type, bk.booking_date, bk.ticket_type,
                      bk.ticket_price, bk.created_at, bk.slot_amount, bk.ticket_status, bk.qr_code, bk.total_price,
                      bk.expired_date, bk.created_at, ui.email, mo.contact_number
        FROM bookings bk
        INNER JOIN museum_owners mo ON mo.museum_id = bk.museum_id
        INNer JOIN visitors vt ON bk.visitor_id = vt.visitor_id
        INNER JOIN user_info ui ON mo.user_id = ui.user_id
        WHERE bk.booking_id = #{bookingId}::UUID
        AND bk.visitor_id = #{visitorId}::UUID;
    """)
    @ResultMap("IndividualBooking")
    BookingV2 retrieveBookingDetailByVisitorId(UUID bookingId, UUID visitorId);

    @Select("""
        SELECT b.* FROM bookings b
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE b.booking_id = #{bookingId}::UUID
    """)
    @ResultMap("IndividualBooking")
    BookingV2 retrieveBookingByBookingId(UUID bookingId);

    // Repository methods for finding bookings
    @Select("""
        SELECT bk.booking_id, mo.museum_id, mo.name, mo.banner_link, bk.booking_type, bk.total_price, bk.ticket_status, mo.description
        FROM bookings bk
        INNER JOIN museum_owners mo ON mo.museum_id = bk.museum_id
        WHERE bk.visitor_id = #{visitorId}::UUID
          AND LOWER(mo.name) LIKE LOWER(CONCAT('%', #{search}, '%'))
        ORDER BY bk.booking_date DESC
        LIMIT #{size} OFFSET #{page} * #{size}
    """)
    @ResultMap("IndividualBooking")
    List<BookingV2> findVisitorBookingHistoryBySearch(
            @Param("visitorId") UUID visitorId,
            @Param("search") String search,
            @Param("page") Integer page,
            @Param("size") Integer size
    );

    @Select("""
        SELECT bk.booking_id, mo.museum_id, mo.name, mo.banner_link, bk.booking_type, bk.total_price, bk.ticket_status, mo.description
        FROM bookings bk
        INNER JOIN museum_owners mo ON mo.museum_id = bk.museum_id
        WHERE bk.visitor_id = #{visitorId}::UUID
        AND LOWER(mo.name) LIKE LOWER(CONCAT('%', #{search}, '%'))
        AND bk.booking_type = #{category}::text
        ORDER BY bk.booking_date DESC
        LIMIT #{size} OFFSET #{page} * #{size}
    """)
    @ResultMap("IndividualBooking")
    List<BookingV2> findVisitorBookingHistoryBySearchAndCategory(
            @Param("visitorId") UUID visitorId,
            @Param("search") String search,
            @Param("category") BookingType category,
            @Param("page") Integer page,
            @Param("size") Integer size
    );

    @Select("""
    SELECT bk.booking_id, mo.museum_id, mo.name, mo.banner_link, bk.booking_type, bk.total_price, bk.ticket_status, mo.description
    FROM bookings bk
    INNER JOIN museum_owners mo ON mo.museum_id = bk.museum_id
    WHERE bk.visitor_id = #{visitorId}::UUID
      AND LOWER(mo.name) LIKE LOWER(CONCAT('%', #{search}, '%'))
      AND bk.booking_date BETWEEN #{startDate}::date AND #{endDate}::date
    ORDER BY bk.booking_date DESC
    LIMIT #{size} OFFSET #{page} * #{size}
""")
    @ResultMap("IndividualBooking")
    List<BookingV2> findVisitorBookingHistoryBySearchAndDateRange(
            @Param("visitorId") UUID visitorId,
            @Param("search") String search,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("page") Integer page,
            @Param("size") Integer size
    );

    @Select("""
    SELECT bk.booking_id, mo.museum_id, mo.name, mo.banner_link, bk.booking_type, bk.total_price, bk.ticket_status, mo.description
    FROM bookings bk
    INNER JOIN museum_owners mo ON mo.museum_id = bk.museum_id
    WHERE bk.visitor_id = #{visitorId}::UUID
      AND LOWER(mo.name) LIKE LOWER(CONCAT('%', #{search}, '%'))
      AND bk.booking_type = #{category}::text
      AND bk.booking_date BETWEEN #{startDate}::date AND #{endDate}::date
    ORDER BY bk.booking_date DESC
    LIMIT #{size} OFFSET #{page} * #{size}
""")
    @ResultMap("IndividualBooking")
    List<BookingV2> findVisitorBookingHistoryBySearchCategoryAndDateRange(
            @Param("visitorId") UUID visitorId,
            @Param("search") String search,
            @Param("category") BookingType category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("page") Integer page,
            @Param("size") Integer size
    );

    // Repository methods for counting (same WHERE conditions, but COUNT(*))
    @Select("""
    SELECT COUNT(*)
    FROM bookings bk
    INNER JOIN museum_owners mo ON mo.museum_id = bk.museum_id
    WHERE bk.visitor_id = #{visitorId}::UUID
      AND LOWER(mo.name) LIKE LOWER(CONCAT('%', #{search}, '%'))
""")
    Integer countVisitorBookingHistoryBySearch(
            @Param("visitorId") UUID visitorId,
            @Param("search") String search
    );

    @Select("""
    SELECT COUNT(*)
    FROM bookings bk
    INNER JOIN museum_owners mo ON mo.museum_id = bk.museum_id
    WHERE bk.visitor_id = #{visitorId}::UUID
      AND LOWER(mo.name) LIKE LOWER(CONCAT('%', #{search}, '%'))
      AND bk.booking_type = #{category}::text
""")
    Integer countVisitorBookingHistoryBySearchAndCategory(
            @Param("visitorId") UUID visitorId,
            @Param("search") String search,
            @Param("category") BookingType category
    );

    @Select("""
    SELECT COUNT(*)
    FROM bookings bk
    INNER JOIN museum_owners mo ON mo.museum_id = bk.museum_id
    WHERE bk.visitor_id = #{visitorId}::UUID
      AND LOWER(mo.name) LIKE LOWER(CONCAT('%', #{search}, '%'))
      AND bk.booking_date BETWEEN #{startDate}::date AND #{endDate}::date
""")
    Integer countVisitorBookingHistoryBySearchAndDateRange(
            @Param("visitorId") UUID visitorId,
            @Param("search") String search,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Select("""
    SELECT COUNT(*)
    FROM bookings bk
    INNER JOIN museum_owners mo ON mo.museum_id = bk.museum_id
    WHERE bk.visitor_id = #{visitorId}::UUID
      AND LOWER(mo.name) LIKE LOWER(CONCAT('%', #{search}, '%'))
      AND bk.booking_type = #{category}::text
      AND bk.booking_date BETWEEN #{startDate}::date AND #{endDate}::date
""")
    Integer countVisitorBookingHistoryBySearchCategoryAndDateRange(
            @Param("visitorId") UUID visitorId,
            @Param("search") String search,
            @Param("category") BookingType category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Select("""
        SELECT bk.booking_id,
               vt.full_name,
               bk.total_price,
               bk.booking_type,
               bk.ticket_type,
               bk.ticket_status,
               bk.booking_date,
               bk.expired_date
        FROM bookings bk
        INNER JOIN visitors vt ON vt.visitor_id = bk.visitor_id
        INNER JOIN museum_owners mo ON mo.museum_id = bk.museum_id
        LEFT JOIN tours tr ON bk.booking_id = tr.booking_id
        WHERE mo.museum_id = '5a307e3d-b052-4cd9-859c-7abdd5a86154'
        AND (
             -- Include INDIVIDUAL bookings (no tours)
             bk.booking_type = 'INDIVIDUAL'
             OR
             -- Include TOUR bookings only if tour status is PAID
             (bk.booking_type = 'TOUR' AND tr.status = 'PAID')
             OR
             -- Include TOUR bookings that don't have tour records yet
             (bk.booking_type = 'TOUR' AND tr.booking_id IS NULL)
             )
          AND LOWER(vt.full_name) LIKE LOWER(CONCAT('%', #{search}, '%'))
        ORDER BY bk.booking_date DESC
        LIMIT #{size} OFFSET #{page} * #{size};
    """)
    @ResultMap("IndividualBooking")
    List<BookingV2> getMuseumBookingHistoryByMuseumId(UUID museumId, String search, Integer page, Integer size);

    @Select("""
        SELECT COUNT(*)
        FROM bookings bk
        INNER JOIN visitors vt ON vt.visitor_id = bk.visitor_id
        INNER JOIN museum_owners mo ON mo.museum_id = bk.museum_id
        WHERE mo.museum_id = #{museumId}::UUID
          AND LOWER(vt.full_name) LIKE LOWER(CONCAT('%', #{search}, '%'));
    """)
    Integer countMuseumBookingHistory(UUID museumId, String search);

    @Select("""
        SELECT mo.museum_id, ticket_info_id, ti.local_price, ti.foreign_price, ti.total_slot
        FROM museum_owners mo
                 LEFT JOIN ticket_info ti ON mo.museum_id = ti.museum_id
        WHERE mo.museum_id = #{museumId}::UUID
        AND mo.is_approved = true;
    """)
    @Results(id = "Individual", value = {
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "ticketId", column = "ticket_info_id"),
            @Result(property = "localPrice", column = "local_price"),
            @Result(property = "foreignPrice", column = "foreign_price"),
            @Result(property = "totalSlots", column = "total_slot"),
            @Result(property = "museumSchedule", column = "museum_id",
                    many = @Many(select = "getScheduleByMuseumId")),
    })
    IndividualBookingInfo BooingIndividualInfo(UUID museumId);

    @Select("""
        SELECT schedule_id, day, opening_time, closing_time, day_off FROM schedules
        WHERE museum_id = #{museumId}::UUID;
    """)
    @Results(id = "MuseumSchedule", value = {
            @Result(property = "scheduleId", column = "schedule_id"),
            @Result(property = "day", column = "day"),
            @Result(property = "openingTime", column = "opening_time"),
            @Result(property = "closingTime", column = "closing_time"),
            @Result(property = "dayOff", column = "day_off"),
    })
    List<MuseumSchedule> getScheduleByMuseumId(UUID museumId);

    @Select("""
        INSERT INTO bookings (
            museum_id, visitor_id, ticket_price, ticket_type,
            ticket_status, booking_type, slot_amount, booking_date,
            expired_date, qr_code, total_price
        )
        VALUES (
            #{museumId}::UUID, #{visitorId}::UUID,
            #{bookingRequest.ticketPrice}, #{ticketType},
            default, 'INDIVIDUAL',
            #{bookingRequest.slotAmount}, #{bookingRequest.bookingDate},
            #{expiredDate}, #{code},
            #{bookingRequest.ticketPrice} * #{bookingRequest.slotAmount}
        )
        RETURNING *;
    """)
    @Results(id = "IndividualBooking", value = {
            @Result(property = "bookingId", column = "booking_id"),
            @Result(property = "tourId", column = "tour_id"),
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "museumName", column = "name"),
            @Result(property = "museumDescription", column = "description"),
            @Result(property = "museumContactNumber", column = "contact_number"),
            @Result(property = "visitorId", column = "visitor_id"),
            @Result(property = "visitorName", column = "full_name"),
            @Result(property = "bookingType", column = "booking_type"),
            @Result(property = "bookingDate", column = "booking_date"),
            @Result(property = "ticketType", column = "ticket_type"),
            @Result(property = "purchasedDate", column = "created_at"),
            @Result(property = "ticketPrice", column = "ticket_price"),
            @Result(property = "slotAmount", column = "slot_amount"),
            @Result(property = "qrCode", column = "qr_code"),
            @Result(property = "totalPrice", column = "total_price"),
            @Result(property = "ticketStatus", column = "ticket_status"),
            @Result(property = "tourStatus", column = "status"),
            @Result(property = "expiredDate", column = "expired_date"),
            @Result(property = "museumBanner", column = "banner_link"),
            @Result(property = "museumEmail", column = "email"),
    })
    BookingV2 insertBookingIndividual(
            UUID museumId, UUID visitorId,
            TicketType ticketType, @Param("bookingRequest") BookingRequestV2 bookingRequest,
            String code, LocalDateTime expiredDate);

    @Select("""
        SELECT bk.*, tb.tour_id, mo.name, tb.status
            FROM bookings bk
            LEFT JOIN tours tb ON bk.booking_id = tb.booking_id
            LEFT JOIN museum_owners mo ON bk.museum_id = mo.museum_id
            WHERE bk.booking_id = #{bookingId}::UUID
            AND bk.visitor_id = #{visitorId}::UUID
    """)
    @Results(id = "TourBooking", value = {
            @Result(property = "bookingId", column = "booking_id"),
            @Result(property = "tourId", column = "tour_id"),
            @Result(property = "museumName", column = "name"),
            @Result(property = "visitorId", column = "visitor_id"),
            @Result(property = "bookingType", column = "booking_type"),
            @Result(property = "bookingDate", column = "booking_date"),
            @Result(property = "ticketType", column = "ticket_type"),
            @Result(property = "purchasedDate", column = "created_at"),
            @Result(property = "ticketPrice", column = "ticket_price"),
            @Result(property = "slotAmount", column = "slot_amount"),
            @Result(property = "qrCode", column = "qr_code"),
            @Result(property = "totalPrice", column = "total_price"),
            @Result(property = "ticketStatus", column = "ticket_status"),
            @Result(property = "tourStatus", column = "status"),
            @Result(property = "expiredDate", column = "expired_date"),
            @Result(property = "guideList", column = "tour_id",
                    many = @Many(select = "org.hrd.finalprojectmuseum.repository.GuideRepository.getGuidesByTourId")),
    })
    BookingV2 getBookingByBookingId(UUID bookingId, UUID visitorId);

    @Select("""
        SELECT COUNT(b.booking_id) FROM bookings b
        LEFT JOIN tours t ON b.booking_id = t.booking_id
        WHERE (t.booking_id IS NULL OR (t.booking_id IS NOT NULL AND t.status = 'PAID'))
        AND b.created_at >= #{startDate} AND b.created_at <= #{endDate}
    """)
    Integer countBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Select("""
        SELECT COUNT(booking_id) FROM bookings WHERE museum_id = #{museumId}::UUID
        AND created_at BETWEEN #{startDate} AND #{endDate}
    """)
    Integer countNewBookingByMuseumId(UUID museumId, LocalDate startDate, LocalDate endDate);

    @Select("""
        SELECT * FROM bookings
        WHERE visitor_id = #{visitorId}::UUID
    """)
    @ResultMap("IndividualBooking")
    List<BookingV2> findBookingByVisitorId(@Param("visitorId") UUID visitorId);

    @Select("""
        SELECT * FROM bookings
        WHERE museum_id = #{museumId}::UUID
    """)
    @ResultMap("IndividualBooking")
    List<BookingV2> findBookingByMuseumId(@Param("museumId") UUID museumId);


    Integer countNewBookingByMuseumId(UUID museumId, LocalDateTime startDate, LocalDateTime endDate);

//    @Select("""
//        SELECT
//            (SELECT COUNT(DISTINCT bk.visitor_id)
//             FROM bookings bk
//             WHERE bk.ticket_type = 'LOCAL'
//               AND bk.museum_id = 'aa9e7772-409d-403c-b835-4ed4e1f1ab9d'
//               AND DATE(bk.created_at) = CURRENT_DATE -7) AS local_Visitors,
//
//            (SELECT COUNT(DISTINCT bk.visitor_id)
//             FROM bookings bk
//             WHERE bk.ticket_type = 'FOREIGNER'
//               AND bk.museum_id = 'aa9e7772-409d-403c-b835-4ed4e1f1ab9d'
//               AND DATE(bk.created_at) = CURRENT_DATE -7) AS foreigner_Visitors,
//
//            (SELECT COUNT(DISTINCT bk.visitor_id)
//             FROM bookings bk
//             WHERE bk.booking_type = 'TOUR'
//               AND bk.museum_id = 'aa9e7772-409d-403c-b835-4ed4e1f1ab9d'
//               AND DATE(bk.created_at) = CURRENT_DATE -7) AS tour_Visitors,
//
//            (SELECT COUNT(bk.ticket_type)
//             FROM bookings bk
//             WHERE bk.ticket_type = 'LOCAL'
//               AND bk.museum_id = 'aa9e7772-409d-403c-b835-4ed4e1f1ab9d') AS total_Locals,
//
//            (SELECT COUNT(bk.ticket_type)
//             FROM bookings bk
//             WHERE bk.ticket_type = 'FOREIGNER'
//               AND bk.museum_id = 'aa9e7772-409d-403c-b835-4ed4e1f1ab9d') AS total_Foreigners,
//
//            (SELECT COUNT(bk.ticket_type)
//             FROM bookings bk
//             WHERE bk.booking_type = 'TOUR'
//               AND bk.museum_id = 'aa9e7772-409d-403c-b835-4ed4e1f1ab9d') AS total_Tours;
//    """)
//    @Results(id = "BookingAnalyticMapper", value = {
//            @Result(property = "localVisitors", column = "local_Visitors"),
//            @Result(property = "foreignerVisitors", column = "foreigner_Visitors"),
//            @Result(property = "tourVisitors", column = "tour_Visitors"),
//            @Result(property = "totalLocals", column = "total_Locals"),
//            @Result(property = "totalForeigners", column = "total_Foreigners"),
//            @Result(property = "totalTours", column = "total_Tours"),
//    })
//    BookingAnalytics findBookingAnalytics(UUID bookingId);
}
package org.hrd.finalprojectmuseum.repository;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.request.TicketInfoRequest;
import org.hrd.finalprojectmuseum.model.entity.TicketInfo;

import java.time.LocalDate;
import java.util.UUID;

@Mapper
public interface TicketInfoRepository {

    @Results(id = "ticketMapper", value = {
            @Result(property = "ticketInfoId", column = "ticket_info_id"),
            @Result(property = "museum", column = "museum_id",
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.MuseumRepository.findMuseumByMuseumId")
            ),
            @Result(property = "localPrice", column = "local_price"),
            @Result(property = "foreignPrice", column = "foreign_price"),
            @Result(property = "totalSlot", column = "total_slot"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
    })
    @Select("""
        SELECT * FROM ticket_info WHERE museum_id = #{museumId}::UUID
    """)
    TicketInfo findTicketInfoByMuseumId(UUID museumId);

    @Update("""
        INSERT INTO ticket_info(museum_id)
        VALUES (#{museumId}::UUID)
    """)
    void insertTicketInfo(UUID museumId);

    @ResultMap("ticketMapper")
    @Select("""
        UPDATE ticket_info SET local_price = #{ticket.localPrice}, foreign_price = #{ticket.foreignPrice}, total_slot = #{ticket.totalSlot} WHERE museum_id = #{museumId}::UUID RETURNING *;
    """)
    TicketInfo modifyTicketInfo(UUID museumId, @Param("ticket") TicketInfoRequest ticketInfoRequest);

    @Update("""
        UPDATE ticket_info SET total_slot = #{slotAmount} WHERE museum_id = #{museumId}::UUID;
    """)
    void updateSlotAmount(UUID museumId, Integer slotAmount);

    @Select("""
        SELECT COUNT(b.booking_id) FROM bookings b
        LEFT JOIN tours t ON t.booking_id = b.booking_id
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE (t.tour_id IS NULL OR (t.tour_id IS NOT NULL AND t.status = 'PAID'))
        AND b.museum_id = #{museumId}::UUID AND m.is_approved = true
        AND b.created_at <= #{today}
    """)
    Integer countTotalSoldByMuseumId(UUID museumId, LocalDate today);

    @Select("""
        SELECT COUNT(b.booking_id) FROM bookings b
        LEFT JOIN tours t ON t.booking_id = b.booking_id
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE (t.tour_id IS NULL OR (t.tour_id IS NOT NULL AND t.status = 'PAID'))
        AND b.museum_id = #{museumId}::UUID AND m.is_approved = true
        AND (b.created_at >= #{startDate} AND b.created_at <= #{endDate})
    """)
    Integer countTotalSoldByMuseumIdAndDateRange(UUID museumId, LocalDate startDate, LocalDate endDate);

    @Select("""
        SELECT SUM(b.slot_amount) FROM bookings b
        LEFT JOIN tours t ON t.booking_id = b.booking_id
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE (t.tour_id IS NULL OR (t.tour_id IS NOT NULL AND t.status = 'PAID'))
        AND b.museum_id = #{museumId}::UUID AND m.is_approved = true
        AND b.created_at <= #{today}
    """)
    Integer countTotalTicketSoldByMuseumId(UUID museumId, LocalDate today);

    @Select("""
        SELECT SUM(b.slot_amount) FROM bookings b
        LEFT JOIN tours t ON t.booking_id = b.booking_id
        INNER JOIN museum_owners m ON b.museum_id = m.museum_id
        WHERE (t.tour_id IS NULL OR (t.tour_id IS NOT NULL AND t.status = 'PAID'))
        AND b.museum_id = #{museumId}::UUID AND m.is_approved = true
        AND (b.created_at >= #{startDate} AND b.created_at <= #{endDate})
    """)
    Integer countTotalTicketSoldByMuseumIdAndRangeDate(UUID museumId, LocalDate startDate, LocalDate endDate);
}
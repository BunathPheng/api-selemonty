package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.request.TicketInfoRequest;
import org.hrd.finalprojectmuseum.model.entity.TicketInfo;

import java.util.UUID;

@Mapper
public interface TicketInfoRepository {

    @Results(id = "ticketMapper", value = {
            @Result(property = "ticketInfoId", column = "ticket_info_id"),
            @Result(property = "museum", column = "museum_id",
                    many = @Many(select = "org.hrd.finalprojectmuseum.repository.MuseumOwnerRepository.findMuseumByMuseumId")
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
}
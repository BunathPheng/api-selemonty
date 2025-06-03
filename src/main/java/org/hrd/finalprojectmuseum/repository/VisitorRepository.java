package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;

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
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
        SELECT * FROM visitors v INNER JOIN bookings b
        ON v.visitor_id = b.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND full_name ILIKE CONCAT('%', #{seach}, '%')
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<Visitor> findVisitorByMuseumId(UUID museumId, String search, Integer page, Integer size);

    @Select("""
        SELECT COUNT(*) FROM visitors v INNER JOIN bookings b
        ON v.visitor_id = b.visitor_id
        WHERE b.museum_id = #{museumId}::UUID
        AND full_name ILIKE CONCAT('%', #{seach}, '%')
    """)
    Integer countAllVisitor(UUID museumId, String search);

    @ResultMap("visitorMapper")
    @Select("""
        SELECT * FROM visitors
        WHERE full_name ILIKE CONCAT('%', #{seach}, '%')
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    ListResponse<Visitor> findAllVisitor(String search, Integer page, Integer size);

    @ResultMap("visitorMapper")
    @Select("""
        SELECT * FROM visitors WHERE visitor_id = #{visitorId}::UUID;
    """)
    Visitor findVisitorById(UUID visitorId);
}

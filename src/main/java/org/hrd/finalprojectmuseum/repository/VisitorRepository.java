package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;

import java.time.LocalDateTime;
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
        SELECT * FROM visitors WHERE user_id = #{userId}::UUID
    """)
    Visitor findVisitor(UUID userId);

    @ResultMap("visitorMapper")
    @Select("""
       UPDATE visitors SET full_name = #{visitor.fullName}, contact_number = #{visitor.contactNumber}, gender = #{visitor.gender},
                           dob = #{visitor.dob}, profile_image_link = #{visitor.profileImageLink}, updated_at = #{updatedAt}  WHERE user_id = #{userId}::uuid
                            RETURNING *;
    """)
    Visitor modifyVisitorByVisitorId(UUID userId, @Param("visitor") VisitorRequest visitorRequest, LocalDateTime updatedAt);

    @ResultMap("visitorMapper")
    @Select("""
        SELECT * FROM visitors WHERE visitor_id = #{visitorId}::UUID
    """)
    Visitor findVisitorById(UUID visitorId);
}

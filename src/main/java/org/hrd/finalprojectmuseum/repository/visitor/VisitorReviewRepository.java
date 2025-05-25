package org.hrd.finalprojectmuseum.repository.visitor;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorReviewRequest;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReview;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface VisitorReviewRepository {
    @Select("""
        SELECT visitor_id
        FROM visitors
        WHERE user_id = #{userId}::UUID
    """)
    UUID retrieveVisitorIDbyUserID(UUID userId);

    @Select("""
        SELECT EXISTS(
        SELECT 1
        FROM museum_owners
        WHERE museum_id = #{museumId}::UUID
    );
    """)
    boolean retrieveMuseumId(UUID museumId);

    @Select("""
       INSERT INTO reviews(museum_id, visitor_id, comment, rating, updated_at)
       VALUES (#{museumId}::UUID, #{visitorId}::UUID, #{visitorReview.comment}, #{visitorReview.rating}, #{updatedAt})
       RETURNING *;
   """)
    @Results(id = "visitorReview", value = {
            @Result(property = "reviewId", column = "review_id"),
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "visitorId", column = "visitor_id"),
            @Result(property = "comment", column = "comment"),
            @Result(property = "rating", column = "rating"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    VisitorReview createVisitorReview(UUID museumId, UUID visitorId, @Param("visitorReview") VisitorReviewRequest visitorReview, LocalDateTime updatedAt);

    @Select("""
        SELECT * FROM reviews
        WHERE museum_id = #{museumId}::UUID;
    """)
    @ResultMap("visitorReview")
    List<VisitorReview> retrieveAllVisitorReviews(UUID museumId);
}

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
            @Result(property = "fullName", column = "visitor_id",
                    one = @One(select = "retrieveVisitorName")),
            @Result(property = "comment", column = "comment"),
            @Result(property = "rating", column = "rating"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    VisitorReview createVisitorReview(
            UUID museumId,
            UUID visitorId,
            @Param("visitorReview") VisitorReviewRequest visitorReview,
            LocalDateTime updatedAt);

    @Select("""
        SELECT * FROM reviews
        WHERE museum_id = #{museumId}::UUID
        ORDER BY created_at DESC
        LIMIT #{size} OFFSET #{offset};
    """)
    @Results(id = "visitorReviewInline", value = {
            @Result(property = "reviewId", column = "review_id"),
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "fullName", column = "visitor_id",
                    one = @One(select = "retrieveVisitorName")),
            @Result(property = "comment", column = "comment"),
            @Result(property = "rating", column = "rating"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<VisitorReview> retrieveAllVisitorReviewsRecently(
            @Param("museumId") UUID museumId,
            @Param("size") Integer size,
            @Param("offset") Integer offset);

    @Select("""
        SELECT full_name FROM visitors
        WHERE visitor_id = #{visitorId}::UUID
    """)
    String retrieveVisitorName(UUID visitorId);

    @Select("""
        SELECT * FROM reviews
        WHERE museum_id = #{museumId}::UUID
        ORDER BY rating DESC
        LIMIT #{size} OFFSET #{offset};
    """)
    @ResultMap("visitorReviewInline")
    List<VisitorReview> retrieveAllVisitorReviewsHighest(
            @Param("museumId") UUID museumId,
            @Param("size") Integer size,
            @Param("offset") Integer offset
    );

    @Select("""
        SELECT * FROM reviews
        WHERE museum_id = #{museumId}::UUID
        ORDER BY rating ASC
        LIMIT #{size} OFFSET #{offset};
    """)
    @ResultMap("visitorReviewInline")
    List<VisitorReview> retrieveAllVisitorReviewsLowest(
            @Param("museumId") UUID museumId,
            @Param("size") Integer size,
            @Param("offset") Integer offset
    );

    @Select("""
        SELECT COUNT(*) FROM reviews
        WHERE museum_id = #{museumId}::UUID
    """)
    Integer countAllVisitorReviews(UUID museumId);

    @Select("""
        SELECT EXISTS(
        SELECT 1
        FROM reviews
        WHERE review_id = #{reviewId}::UUID
    );
    """)
    boolean retrieveReviewId(UUID reviewId);

    @Select("""
        SELECT EXISTS(
        SELECT 1
        FROM reviews
        WHERE visitor_id = #{visitorId}::UUID
    );
    """)
    boolean retrieveVisitorId(UUID visitorId);

    @Select("""
        UPDATE reviews
        SET comment = #{visitorReview.comment}, rating = #{visitorReview.rating}, updated_at = #{updatedAt}
        WHERE review_id = #{reviewId}::UUID AND visitor_id = #{visitorId}::UUID
        RETURNING *;
    """)
    @ResultMap("visitorReviewInline")
    VisitorReview updateVisitorReview(
            @Param("reviewId") UUID reviewId,
            @Param("visitorId") UUID visitorId,
            @Param("visitorReview") VisitorReviewRequest visitorReviewRequest,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Select("""
        DELETE FROM reviews
        WHERE review_id = #{reviewId}::UUID AND visitor_id = #{visitorId}::UUID;
    """)
    void deleteVisitorReview(UUID reviewId, UUID visitorId);



}

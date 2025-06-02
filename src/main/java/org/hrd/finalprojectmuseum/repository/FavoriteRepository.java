package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorFavorite;

import java.util.UUID;

@Mapper
public interface FavoriteRepository {

    @Select("""
        SELECT EXISTS(
        SELECT 1
        FROM museum_owners
        WHERE museum_id = #{museumId}::UUID AND is_approved = true
        )
    """)
    boolean isApproveMuseum(UUID museumId);

    @Insert("""
        INSERT INTO favorites(museum_id, visitor_id, is_favorite)
        VALUES (#{museumId}::UUID, #{visitorId}::UUID, #{isFavorite})
    """)
    void addVisitorFavorite(@Param("museumId") UUID museumId, @Param("visitorId") UUID visitorId, boolean isFavorite);

    @Update("""
        UPDATE favorites
        SET is_favorite = #{isFavorite}
        WHERE museum_id = #{museumId}::UUID AND visitor_id = #{visitorId}::UUID
    """)
    void updateVisitorFavorite(@Param("museumId") UUID museumId, @Param("visitorId") UUID visitorId, boolean isFavorite);

    @Select("""
        SELECT EXISTS(
        SELECT 1
        FROM favorites
        WHERE museum_id = #{museumId}::UUID AND visitor_id = #{visitorId}::UUID
        AND is_favorite = true
        )
    """)
    boolean isMuseumFavoriteByVisitor(@Param("museumId") UUID museumId, @Param("visitorId") UUID visitorId);

    @Select("""
        SELECT EXISTS(
        SELECT 1
        FROM favorites
        WHERE museum_id = #{museumId}::UUID AND visitor_id = #{visitorId}::UUID
        AND is_favorite = false
        )
    """)
    boolean isMuseumUnFavoriteByVisitor(@Param("museumId") UUID museumId, @Param("visitorId") UUID visitorId);

    @Select("""
        SELECT favorite_id, museum_id, visitor_id, is_favorite, created_at
        FROM favorites
        WHERE museum_id = #{museumId}::UUID AND visitor_id = #{visitorId}::UUID
    """)
    @Results({
            @Result(property = "favoriteId", column = "favorite_id"),
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "visitorId", column = "visitor_id"),
            @Result(property = "isFavorite", column = "is_favorite"),
            @Result(property = "createdAt", column = "created_at")
    })
    VisitorFavorite getFavoriteByIds(@Param("museumId") UUID museumId, @Param("visitorId") UUID visitorId);
}

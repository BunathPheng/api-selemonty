package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.FavoriteMuseum;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.FavoriteMuseumSchedule;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorFavorite;

import java.util.List;
import java.util.UUID;

@Mapper
public interface FavoriteRepository {

    @Select("""
        SELECT is_approved
        FROM museum_owners
        WHERE museum_id = #{museumId}::UUID
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

    @Select("""
        SELECT mo.museum_id, mo.name, mo.address, mo.logo_link, ui.email, f.is_favorite
        FROM museum_owners mo
        INNER JOIN user_info ui ON mo.user_id = ui.user_id
        INNER JOIN favorites f ON f.museum_id = mo.museum_id
        WHERE f.visitor_id = #{visitorId}::UUID;
    """)
    @Results(id = "AllFavoriteMuseums", value = {
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "museumName", column = "name"),
            @Result(property = "museumEmail", column = "email"),
            @Result(property = "logoLink", column = "logo_link"),
            @Result(property = "museumAddress", column = "address"),
            @Result(property = "isFavorite", column = "is_favorite"),
            @Result(property = "favoriteMuseumSchedule", column = "museum_id",
                    many = @Many(select = "getFavoriteMuseumSchedule")
            )
    })
    List<FavoriteMuseum> retrieveFavoriteMuseums(UUID visitorId);

    @Select("""
        SELECT day, opening_time, closing_time
        FROM schedules
        WHERE museum_id = #{museumId}::UUID
        AND day_off = true;
    """)
    @Results({
            @Result(property = "day", column = "day"),
            @Result(property = "openingTime", column = "opening_time"),
            @Result(property = "closingTime", column = "closing_time")
    })
    List<FavoriteMuseumSchedule> getFavoriteMuseumSchedule(UUID museumId);
}

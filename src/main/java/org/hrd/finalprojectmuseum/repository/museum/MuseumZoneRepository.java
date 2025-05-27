package org.hrd.finalprojectmuseum.repository.museum;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneUpdateRequest;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumZoneResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface MuseumZoneRepository {

    @Select("""
        SELECT * FROM zone_categories;
    """)
    @Results(id = "zoneCategory", value = {
            @Result(property = "museumZoneCategoryId", column = "zone_category_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<MuseumZoneCategory> getAllMuseumZoneCategories();

    @Select("""
        SELECT museum_id
        FROM museum_owners
        WHERE user_id = #{userId}::UUID
    """)
    UUID retrieveMuseumIDbyUserID(UUID userId);

    @Select("""
        INSERT INTO museum_zones(museum_id, zone_category_id, name, description, picture_link, video_link, updated_at)
        VALUES (#{museumId}::UUID, #{museum.categoryId}::UUID, #{museum.name}, #{museum.description}, #{museum.pictureLink},
                #{museum.videoLink}, #{updatedAt})
        RETURNING museum_zone_id;
    """)
    UUID createMuseumZone(@Param("museum") MuseumZoneRequest museumZoneRequest, UUID museumId, LocalDateTime updatedAt);

    @Select("""
        SELECT zc.* FROM museum_zones mz
        LEFT JOIN zone_categories zc ON zc.zone_category_id = mz.zone_category_id
        WHERE museum_id = #{museumId}::UUID;
    """)
    @ResultMap("zoneCategory")
    List<MuseumZoneCategory> retrieveAllZonesCategoriesByMuseumID(UUID museumId);

    @Select("""
        SELECT * FROM museum_zones
        WHERE museum_zone_id = #{zoneID}::UUID
        AND is_deleted = false;
    """)
    @Results(id = "zoneDetail", value = {
            @Result(property = "zoneId", column = "museum_zone_id"),
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "zoneCategoryName", column = "zone_category_id",
                    one = @One(select = "retrieveZoneCategoryNameByCategoryId")
            ),
            @Result(property = "zoneName", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "pictureLink", column = "picture_link"),
            @Result(property = "videoLink", column = "video_link"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "isDeleted", column = "is_deleted"),
            @Result(property = "artifacts", column = "museum_zone_id",
                    many = @Many(select = "org.hrd.finalprojectmuseum.repository.museum.MuseumArtifactRepository.retrieveMuseumArtifactByZoneId")
            )
    })
    MuseumZone retrieveMuseumZoneDetailByZoneId(UUID zoneID);

    @Select("""
        SELECT name FROM zone_categories
        WHERE zone_category_id = #{zoneCategoryId}::UUID;
    """)
    String retrieveZoneCategoryNameByCategoryId(UUID categoryId);

    @Update("""
        UPDATE museum_zones
        SET zone_category_id = #{museumZone.categoryId}::UUID, name = #{museumZone.name}, description = #{museumZone.description},
            picture_link = #{museumZone.pictureLink}, video_link = #{museumZone.videoLink}, updated_at = #{updatedAt}
        WHERE museum_zone_id = #{zoneId}::UUID;
    """)
    void updateMuseumZoneDetailByZoneId(UUID zoneId, @Param("museumZone") MuseumZoneUpdateRequest museumZoneUpdateRequest, LocalDateTime updatedAt);

    @Select("""
        SELECT EXISTS(
        SELECT 1
        FROM zone_categories
        WHERE zone_category_id = #{zoneCategoryId}::UUID
    );
    """)
    boolean retrieveMuseumZoneCategoryId(UUID zoneCategoryId);

    @Select("""
        SELECT EXISTS(
        SELECT 1
        FROM museum_zones
        WHERE museum_zone_id = #{museumZoneId}::UUID
        AND is_deleted = false
    );
    """)
    boolean retrieveMuseumZoneId(UUID museumZoneId);

    @Update("""
        UPDATE museum_zones
        SET is_deleted = #{isDeleted}
        WHERE museum_zone_id = #{zoneId}::UUID;
    """)
    void deleteMuseumZoneByZoneId(UUID zoneId, Boolean isDeleted);

    @Select("""
        SELECT mz.museum_zone_id, mz.museum_id, mz.name, mz.description, mz.picture_link, mz.zone_category_id,
               mz.created_at, mz.updated_at
        FROM museum_zones mz
        WHERE mz.museum_id = #{museumId}::UUID
        AND mz.is_deleted = false
        ORDER BY mz.updated_at DESC
        LIMIT #{size} OFFSET #{offset};
    """)
    @Results(id = "zoneListMapping", value = {
            @Result(property = "zoneId", column = "museum_zone_id"),
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "zoneCategoryName", column = "zone_category_id",
                    one = @One(select = "retrieveZoneCategoryNameByCategoryId")
            ),
            @Result(property = "zoneName", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "pictureLink", column = "picture_link"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<MuseumZoneResponse> retrieveMuseumZoneByMuseumId(@Param("museumId") UUID museumId, @Param("size") Integer size, @Param("offset") Integer offset);

    @Select("""
        SELECT COUNT(*)
        FROM museum_zones mz
        WHERE mz.museum_id = #{museumId}::UUID
        AND mz.is_deleted = false;
    """)
    Integer countMuseumZonesByMuseumId(UUID museumId);
}

package org.hrd.finalprojectmuseum.repository.museum;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneUpdateRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;
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
        INSERT INTO artifacts(museum_zone_id, title, description, third_d_model_link, updated_at)
        VALUES (#{museumZoneId}::UUID, #{artifact.title}, #{artifact.description}, #{artifact.thirdDModelLink}, #{updatedAt})
        RETURNING *;
    """)
    @ResultMap("artifact")
    MuseumArtifact createMuseumArtifact(@Param("artifact") MuseumArtifactRequest museumArtifactRequest, UUID museumZoneId, LocalDateTime updatedAt);

    @Select("""
        SELECT zc.* FROM museum_zones mz
        LEFT JOIN zone_categories zc ON zc.zone_category_id = mz.zone_category_id
        WHERE museum_id = #{museumId}::UUID;
    """)
    @ResultMap("zoneCategory")
    List<MuseumZoneCategory> retrieveAllZonesCategoriesByMuseumID(UUID museumId);

    @Select("""
        SELECT * FROM museum_zones
        WHERE museum_zone_id = #{zoneID}::UUID;
    """)
    @Results(id = "zoneDetail", value = {
            @Result(property = "zoneId", column = "museum_zone_id"),
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "zoneCategoryId", column = "zone_category_id"),
            @Result(property = "zoneName", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "pictureLink", column = "picture_link"),
            @Result(property = "videoLink", column = "video_link"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "isDeleted", column = "is_deleted"),
            @Result(property = "artifacts", column = "museum_zone_id",
                    many = @Many(select = "retrieveMuseumArtifactByZoneId")
            )
    })
    MuseumZone retrieveMuseumZoneDetailByZoneId(UUID zoneID);

    @Select("""
        SELECT * FROM artifacts
        WHERE museum_zone_id = #{zoneID}::UUID;
    """)
    @Results(id = "artifact", value = {
            @Result(property = "id", column = "artifact_id"),
            @Result(property = "zoneId", column = "museum_zone_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "thirdDModelLink", column = "third_d_model_link"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "isDeleted", column = "is_deleted")
    })
    MuseumArtifact retrieveMuseumArtifactByZoneId(UUID zoneID);

    @Update("""
        UPDATE museum_zones 
        SET zone_category_id = #{museumZone.categoryId}::UUID, name = #{museumZone.name}, description = #{museumZone.description}, 
            picture_link = #{museumZone.pictureLink}, video_link = #{museumZone.videoLink}, updated_at = #{updatedAt}
        WHERE museum_zone_id = #{zoneId}::UUID;
    """)
    void updateMuseumZoneDetailByZoneId(UUID zoneId, @Param("museumZone") MuseumZoneUpdateRequest museumZoneUpdateRequest, LocalDateTime updatedAt);

    @Update("""
        UPDATE artifacts
        SET title = #{artifact.title}, description = #{artifact.description}, 
            third_d_model_link = #{artifact.thirdDModelLink}, updated_at = #{updatedAt}
        WHERE artifact_id = #{artifactId}::UUID;
    """)
    void updateMuseumArtifactByArtifactId(UUID artifactId, @Param("artifact") MuseumArtifactRequest museumArtifactRequest, LocalDateTime updatedAt);

    @Select("""
        SELECT EXISTS(
        SELECT 1
        FROM artifacts
        WHERE artifact_id = #{artifactId}::UUID
    );
    """)
    boolean retrieveMuseumArtifactId(UUID artifactId);


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
    );
    """)
    boolean retrieveMuseumZoneId(UUID museumZoneId);

}

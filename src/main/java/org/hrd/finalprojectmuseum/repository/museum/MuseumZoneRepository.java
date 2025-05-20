package org.hrd.finalprojectmuseum.repository.museum;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
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
    """)
    void createMuseumArtifact(@Param("artifact")MuseumArtifactRequest museumArtifactRequest, UUID museumZoneId, LocalDateTime updatedAt);
}

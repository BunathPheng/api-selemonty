package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper
public interface ArtifactRepository {

    @Select("""
        INSERT INTO artifacts(museum_zone_id, title, description, third_d_model_link, updated_at)
        VALUES (#{museumZoneId}::UUID, #{artifact.title}, #{artifact.description}, #{artifact.thirdDModelLink}, #{updatedAt})
        RETURNING *;
    """)
    @ResultMap("artifact")
    MuseumArtifact createMuseumArtifact(@Param("artifact") MuseumArtifactRequest museumArtifactRequest, UUID museumZoneId, LocalDateTime updatedAt);

    @Select("""
        SELECT EXISTS(
        SELECT 1
        FROM artifacts
        WHERE artifact_id = #{artifactId}::UUID
        AND is_deleted = false
    );
    """)
    boolean retrieveMuseumArtifactId(UUID artifactId);

    @Update("""
        UPDATE artifacts
        SET title = #{artifact.title}, description = #{artifact.description},
            third_d_model_link = #{artifact.thirdDModelLink}, updated_at = #{updatedAt}
        WHERE artifact_id = #{artifactId}::UUID
    """)
    void updateMuseumArtifactByArtifactId(UUID artifactId, @Param("artifact") MuseumArtifactRequest museumArtifactRequest, LocalDateTime updatedAt);

    @Update("""
        UPDATE artifacts
        SET is_deleted = #{isDeleted}, updated_at = NOW()
        WHERE artifact_id = #{artifactId}::UUID
    """)
    void deleteMuseumArtifactByArtifactId(UUID artifactId, Boolean isDeleted);

    @Update("""
        UPDATE artifacts
        SET is_deleted = #{isDeleted}, updated_at = NOW()
         WHERE museum_zone_id = #{museumZoneId}::UUID
    """)
    void deleteMuseumArtifactByMuseumId(
            @Param("museumZoneId") UUID museumZoneId,
            @Param("isDeleted") Boolean isDeleted
    );

    @Select("""
        SELECT * FROM artifacts
        WHERE museum_zone_id = #{zoneID}::UUID
        AND is_deleted = false;
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

    @Select("""
        SELECT * FROM artifacts
        WHERE artifact_id = #{zoneID}::UUID
        AND is_deleted = false;
    """)
    @ResultMap("artifact")
    MuseumArtifact retrieveMuseumArtifactByArtifactId(UUID zoneID);

}

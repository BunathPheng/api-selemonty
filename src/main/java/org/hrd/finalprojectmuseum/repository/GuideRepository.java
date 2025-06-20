package org.hrd.finalprojectmuseum.repository;

import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.GuideRequest;
import org.hrd.finalprojectmuseum.model.entity.Guide;
import org.hrd.finalprojectmuseum.model.enums.GuideStatusType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface GuideRepository {

    @Results(id = "guideMapper", value = {
            @Result(property = "guideId", column = "guide_id"),
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "guideName", column = "guide_name"),
            @Result(property = "contactNumber", column = "contact_number"),
            @Result(property = "staticQrLink", column = "static_qr_link"),
            @Result(property = "isAvailable", column = "is_available"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "expiresAt", column = "expired_date")
    })
    @Select("""
        SELECT * FROM guides WHERE museum_id = #{museumId}::UUID
        AND guide_name ILIKE CONCAT('%', #{search}, '%')
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<Guide> findAllGuideByMuseumId(UUID museumId, String search, Integer page, Integer size);

    @Select("""
        SELECT COUNT(*) FROM guides WHERE museum_id = #{museumId}::UUID
        AND guide_name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllGuide(UUID museumId, String search);

    @ResultMap("guideMapper")
    @Select("""
        INSERT INTO guides(museum_id, guide_name, contact_number, static_qr_link, is_available)
        VALUES (#{museumId}::UUID, #{guide.guideName}, #{guide.contactNumber}, #{guide.staticQrLink}, #{guide.isAvailable})
        RETURNING *
    """)
    Guide insertGuideByMuseumId(UUID museumId, @Param("guide") GuideRequest guideRequest);

    @ResultMap("guideMapper")
    @Select("""
        SELECT * FROM guides WHERE guide_id = #{guideId}::UUID;
    """)
    Guide findGuideByGuideId(UUID guideId);

    @ResultMap("guideMapper")
    @Select("""
        UPDATE guides SET
            guide_name = #{guide.guideName},
            contact_number = #{guide.contactNumber},
            static_qr_link = #{guide.staticQrLink},
            is_available = #{guide.isAvailable},
            updated_at = #{updatedAt}
        WHERE museum_id = #{museumId}::UUID AND guide_id = #{guideId}::UUID
        RETURNING *
    """)
    Guide modifyGuideByGuideId(UUID museumId, UUID guideId, @Param("guide") GuideRequest guideRequest, LocalDateTime updatedAt);

    @ResultMap("guideMapper")
    @Select("""
        SELECT * FROM guides WHERE museum_id = #{museumId}::UUID
        AND guide_name ILIKE CONCAT('%', #{search}, '%')
        AND is_available = #{isAvailable}
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<Guide> findAllGuideByMuseumIdWithType(UUID museumId, String search, Integer page, Integer size, Boolean isAvailable);

    @Select("""
        SELECT COUNT(*) FROM guides WHERE museum_id = #{museumId}::UUID
        AND guide_name ILIKE CONCAT('%', #{search}, '%')
        AND is_available = #{isAvailable}
    """)
    Integer countAllGuideWithType(UUID museumId, String search, Boolean isAvailable);

    @Select("""
    SELECT guide_id, is_available 
    FROM guides 
    WHERE museum_id = #{museumId}::UUID
    """)
    @ResultMap("guideMapper")
    List<Guide> getGuideStatus(UUID museumId);

    @Select("""
        SELECT gd.*
            FROM guides gd
            INNER JOIN tour_guides tg ON gd.guide_id = tg.guide_id
        WHERE tg.tour_id = #{tourId}::UUID
    """)
    @ResultMap("guideMapper")
    List<Guide> getGuidesByTourId(UUID tourId);

    @Select("""
        SELECT gd.*, bk.expired_date
        FROM guides gd
                 INNER JOIN tour_guides tg ON gd.guide_id = tg.guide_id
                INNER JOIN tours ts ON tg.tour_id = ts.tour_id
                INNER JOIN bookings BK ON ts.booking_id = BK.booking_id
                INNER JOIN museum_owners mo ON BK.museum_id = mo.museum_id
        WHERE mo.museum_id = #{museumId}::UUID
    """)
    @ResultMap("guideMapper")
    List<Guide> getGuidesByMuseumId(UUID museumId);

    @Update("""
        UPDATE guides 
        SET is_available = true
        WHERE guide_id = #{guideId}::UUID
    """)
    void updateStatusGuide(UUID guideId);
}

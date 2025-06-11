package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumWithDistanceResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumCategory;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Mapper
public interface MuseumRepository {

    @Results(id = "museumMapper", value = {
            @Result(property = "museumId", column = "museum_id", javaType = UUID.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "appUserRegister", column = "user_id",
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.AppUserRepository.getUserById")),
            @Result(property = "museumCategory", column = "museum_category_id", javaType = UUID.class, jdbcType = JdbcType.VARCHAR,
                    many = @Many(select = "findMuseumCategoryById")),
            @Result(property = "name", column = "name"),
            @Result(property = "isFavorite", column = "is_favorite"),
            @Result(property = "address", column = "address"),
            @Result(property = "contactNumber", column = "contact_number"),
            @Result(property = "lat", column = "lat"),
            @Result(property = "lng", column = "lng"),
            @Result(property = "logoLink", column = "logo_link"),
            @Result(property = "bannerLink", column = "banner_link"),
            @Result(property = "landscapeLink", column = "landscape_links"),
            @Result(property = "description", column = "description"),
            @Result(property = "isApproved", column = "is_approved"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "museumArtifact", column = "museum_id",
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.ArtifactRepository.findMuseumArtifactByMuseumId")
            )
    })
    @Select("""
        SELECT museum_owners.*, 
               CASE WHEN favorites.visitor_id IS NOT NULL THEN true ELSE false END AS is_favorite
        FROM museum_owners
        INNER JOIN user_info ON user_info.user_id = museum_owners.user_id
        LEFT JOIN favorites ON museum_owners.museum_id = favorites.museum_id 
                            AND favorites.visitor_id = #{visitorId}::UUID
        WHERE user_info.is_verified = true AND museum_owners.museum_id = #{museumId}::UUID;
    """)
    MuseumOwner findMuseumOwnerByMuseumId(UUID museumId);

    @ResultMap("museumMapper")
    @Select("""
        SELECT * FROM museum_owners WHERE museum_id = #{museumId}::UUID;
    """)
    MuseumOwner findMuseumByMuseumId(UUID museumId);

    @Result(property = "museumCategoryId", column = "museum_category_id")
    @Select("""
        SELECT museum_category_id, name FROM museum_categories;
    """)
    List<MuseumCategory> getMuseumCategories();

    @Select("""
        SELECT EXISTS(
            SELECT 1
            FROM museum_categories
            WHERE museum_category_id = #{museumCategoryId}::UUID
        )
    """)
    Boolean isMuseumCategoriesExist(UUID museumCategoryId);

    @Result(property = "museumCategoryId", column = "museum_category_id")
    @Select("""
        SELECT museum_category_id, name FROM museum_categories WHERE museum_category_id = #{museumCategoryId}::UUID;
    """)
    MuseumCategory findMuseumCategoryById(UUID museumCategoryId);

    // Latest Sort - Non-visitor methods
    @ResultMap("museumMapper")
    @Select("""
        SELECT museum_owners.*, false AS is_favorite
        FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id
        WHERE is_verified = true AND name ILIKE CONCAT('%', #{search}, '%')
        ORDER BY museum_owners.created_at DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllMuseums(String search, Integer page, Integer size);

    @ResultMap("museumMapper")
    @Select("""
        SELECT museum_owners.*, false AS is_favorite
        FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id 
        WHERE is_approved = #{isApproved} AND is_verified = true
        AND name ILIKE CONCAT('%', #{search}, '%')
        ORDER BY museum_owners.created_at DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllMuseumsWithStatus(String search, Integer page, Integer size, Boolean isApproved);

    @ResultMap("museumMapper")
    @Select("""
        SELECT museum_owners.*, false AS is_favorite
        FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id
        WHERE is_verified = true AND museum_category_id = #{museumCategoryId}::UUID
        AND name ILIKE CONCAT('%', #{search}, '%')
        ORDER BY museum_owners.created_at DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllMuseumsByCategoryId(String search, UUID museumCategoryId, Integer page, Integer size);

    @ResultMap("museumMapper")
    @Select("""
        SELECT museum_owners.*, false AS is_favorite
        FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id
        WHERE is_verified = true AND museum_category_id = #{museumCategoryId}::UUID
        AND name ILIKE CONCAT('%', #{search}, '%') AND is_approved = #{isApproved}
        ORDER BY museum_owners.created_at DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllMuseumsByCategoryIdAndStatus(String search, UUID museumCategoryId, Integer page, Integer size, boolean isApproved);

    // Popular Sort - Non-visitor methods
    @ResultMap("museumMapper")
    @Select("""
        SELECT m.*, COUNT(b.booking_id) AS booking_count, false AS is_favorite
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        LEFT JOIN bookings b ON m.museum_id = b.museum_id
        WHERE is_verified = true
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        GROUP BY m.museum_id, ui.user_id
        ORDER BY booking_count DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllPopularMuseumsPopular(String search, Integer page, Integer size);

    @ResultMap("museumMapper")
    @Select("""
        SELECT m.*, COUNT(b.booking_id) AS booking_count, false AS is_favorite
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        LEFT JOIN bookings b ON m.museum_id = b.museum_id
        WHERE ui.is_verified = true
        AND m.is_approved = #{isApproved}
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        GROUP BY m.museum_id, ui.user_id
        ORDER BY booking_count DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllPopularMuseumsWithStatus(String search, Integer page, Integer size, Boolean isApproved);

    @ResultMap("museumMapper")
    @Select("""
        SELECT m.*, COUNT(b.booking_id) AS booking_count, false AS is_favorite
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        LEFT JOIN bookings b ON m.museum_id = b.museum_id
        WHERE ui.is_verified = true
        AND m.museum_category_id = #{museumCategoryId}::UUID
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        GROUP BY m.museum_id, ui.user_id
        ORDER BY booking_count DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllPopularMuseumsByCategoryId(String search, UUID museumCategoryId, Integer page, Integer size);

    @ResultMap("museumMapper")
    @Select("""
        SELECT m.*, COUNT(b.booking_id) AS booking_count, false AS is_favorite
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        LEFT JOIN bookings b ON m.museum_id = b.museum_id
        WHERE ui.is_verified = true
        AND m.museum_category_id = #{museumCategoryId}::UUID
        AND m.is_approved = #{isApproved}
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        GROUP BY m.museum_id, ui.user_id
        ORDER BY booking_count DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllPopularMuseumsByCategoryIdAndStatus(String search, UUID museumCategoryId, Integer page, Integer size, Boolean isApproved);

    // Latest Sort - Visitor methods with favorites
    @ResultMap("museumMapper")
    @Select("""
        SELECT museum_owners.*, 
               CASE WHEN favorites.visitor_id IS NOT NULL THEN true ELSE false END AS is_favorite
        FROM museum_owners
        INNER JOIN user_info ON user_info.user_id = museum_owners.user_id
        LEFT JOIN favorites ON museum_owners.museum_id = favorites.museum_id 
                            AND favorites.visitor_id = #{visitorId}::UUID
        WHERE user_info.is_verified = true 
        AND museum_owners.name ILIKE CONCAT('%', #{search}, '%')
        ORDER BY museum_owners.created_at DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllMuseumsForVisitor(UUID visitorId, String search, Integer page, Integer size);

    @ResultMap("museumMapper")
    @Select("""
        SELECT museum_owners.*, 
               CASE WHEN favorites.visitor_id IS NOT NULL THEN true ELSE false END AS is_favorite
        FROM museum_owners
        INNER JOIN user_info ON user_info.user_id = museum_owners.user_id
        LEFT JOIN favorites ON museum_owners.museum_id = favorites.museum_id 
                            AND favorites.visitor_id = #{visitorId}::UUID
        WHERE user_info.is_verified = true 
        AND museum_owners.is_approved = #{isApproved}
        AND museum_owners.name ILIKE CONCAT('%', #{search}, '%')
        ORDER BY museum_owners.created_at DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllMuseumsWithStatusForVisitor(UUID visitorId, String search, Integer page, Integer size, Boolean isApproved);

    @ResultMap("museumMapper")
    @Select("""
        SELECT museum_owners.*, 
               CASE WHEN favorites.visitor_id IS NOT NULL THEN true ELSE false END AS is_favorite
        FROM museum_owners
        INNER JOIN user_info ON user_info.user_id = museum_owners.user_id
        LEFT JOIN favorites ON museum_owners.museum_id = favorites.museum_id 
                            AND favorites.visitor_id = #{visitorId}::UUID
        WHERE user_info.is_verified = true 
        AND museum_owners.museum_category_id = #{museumCategoryId}::UUID
        AND museum_owners.name ILIKE CONCAT('%', #{search}, '%')
        ORDER BY museum_owners.created_at DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllMuseumsByCategoryIdForVisitor(UUID visitorId, String search, UUID museumCategoryId, Integer page, Integer size);

    @ResultMap("museumMapper")
    @Select("""
        SELECT museum_owners.*, 
               CASE WHEN favorites.visitor_id IS NOT NULL THEN true ELSE false END AS is_favorite
        FROM museum_owners
        INNER JOIN user_info ON user_info.user_id = museum_owners.user_id
        LEFT JOIN favorites ON museum_owners.museum_id = favorites.museum_id 
                            AND favorites.visitor_id = #{visitorId}::UUID
        WHERE user_info.is_verified = true 
        AND museum_owners.museum_category_id = #{museumCategoryId}::UUID
        AND museum_owners.is_approved = #{isApproved}
        AND museum_owners.name ILIKE CONCAT('%', #{search}, '%')
        ORDER BY museum_owners.created_at DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllMuseumsByCategoryIdAndStatusForVisitor(UUID visitorId, String search, UUID museumCategoryId, Integer page, Integer size, Boolean isApproved);

    // Popular Sort - Visitor methods with favorites
    @ResultMap("museumMapper")
    @Select("""
        SELECT m.*, COUNT(b.booking_id) AS booking_count,
               CASE WHEN favorites.visitor_id IS NOT NULL THEN true ELSE false END AS is_favorite
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        LEFT JOIN bookings b ON m.museum_id = b.museum_id
        LEFT JOIN favorites ON m.museum_id = favorites.museum_id 
                            AND favorites.visitor_id = #{visitorId}::UUID
        WHERE ui.is_verified = true
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        GROUP BY m.museum_id, ui.user_id, favorites.visitor_id
        ORDER BY booking_count DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllPopularMuseumsPopularForVisitor(UUID visitorId, String search, Integer page, Integer size);

    @ResultMap("museumMapper")
    @Select("""
        SELECT m.*, COUNT(b.booking_id) AS booking_count,
               CASE WHEN favorites.visitor_id IS NOT NULL THEN true ELSE false END AS is_favorite
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        LEFT JOIN bookings b ON m.museum_id = b.museum_id
        LEFT JOIN favorites ON m.museum_id = favorites.museum_id 
                            AND favorites.visitor_id = #{visitorId}::UUID
        WHERE ui.is_verified = true
        AND m.is_approved = #{isApproved}
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        GROUP BY m.museum_id, ui.user_id, favorites.visitor_id
        ORDER BY booking_count DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllPopularMuseumsWithStatusForVisitor(UUID visitorId, String search, Integer page, Integer size, Boolean isApproved);

    @ResultMap("museumMapper")
    @Select("""
        SELECT m.*, COUNT(b.booking_id) AS booking_count,
               CASE WHEN favorites.visitor_id IS NOT NULL THEN true ELSE false END AS is_favorite
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        LEFT JOIN bookings b ON m.museum_id = b.museum_id
        LEFT JOIN favorites ON m.museum_id = favorites.museum_id 
                            AND favorites.visitor_id = #{visitorId}::UUID
        WHERE ui.is_verified = true
        AND m.museum_category_id = #{museumCategoryId}::UUID
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        GROUP BY m.museum_id, ui.user_id, favorites.visitor_id
        ORDER BY booking_count DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllPopularMuseumsByCategoryIdForVisitor(UUID visitorId, String search, UUID museumCategoryId, Integer page, Integer size);

    @ResultMap("museumMapper")
    @Select("""
        SELECT m.*, COUNT(b.booking_id) AS booking_count,
               CASE WHEN favorites.visitor_id IS NOT NULL THEN true ELSE false END AS is_favorite
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        LEFT JOIN bookings b ON m.museum_id = b.museum_id
        LEFT JOIN favorites ON m.museum_id = favorites.museum_id 
                            AND favorites.visitor_id = #{visitorId}::UUID
        WHERE ui.is_verified = true
        AND m.museum_category_id = #{museumCategoryId}::UUID
        AND m.is_approved = #{isApproved}
        AND m.name ILIKE CONCAT('%', #{search}, '%')
        GROUP BY m.museum_id, ui.user_id, favorites.visitor_id
        ORDER BY booking_count DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllPopularMuseumsByCategoryIdAndStatusForVisitor(UUID visitorId, String search, UUID museumCategoryId, Integer page, Integer size, Boolean isApproved);

    // ==============================================
    // COUNT METHODS FOR PAGINATION
    // ==============================================

    @Select("""
        SELECT COUNT(*) FROM museum_owners INNER JOIN user_info on user_info.user_id = museum_owners.user_id
        WHERE is_verified = true AND name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllMuseums(String search);

    @Select("""
        SELECT COUNT(*) FROM museum_owners INNER JOIN user_info on user_info.user_id = museum_owners.user_id
        WHERE is_verified = true AND name ILIKE CONCAT('%', #{search}, '%')
        AND is_approved = #{isApproved}
    """)
    Integer countAllMuseumsWithStatus(String search, boolean isApproved);

    @Select("""
        SELECT COUNT(*) FROM museum_owners INNER JOIN user_info on user_info.user_id = museum_owners.user_id
        WHERE is_verified = true AND name ILIKE CONCAT('%', #{search}, '%') AND museum_category_id = #{museumCategoryId}::UUID
    """)
    Integer countAllMuseumsByCategory(String search, UUID museumCategoryId);

    @Select("""
        SELECT COUNT(*) FROM museum_owners INNER JOIN user_info on user_info.user_id = museum_owners.user_id
        WHERE is_verified = true AND name ILIKE CONCAT('%', #{search}, '%')
        AND museum_category_id = #{museumCategoryId}::UUID AND is_approved = #{isApproved}
    """)
    Integer countAllMuseumsByCategoryAndStatus(String search, UUID museumCategoryId, boolean isApproved);

    @Select("""
        SELECT COUNT(DISTINCT m.museum_id)
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        WHERE ui.is_verified = true
        AND m.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllPopularMuseums(String search);

    @Select("""
        SELECT COUNT(DISTINCT m.museum_id)
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        WHERE ui.is_verified = true
        AND m.is_approved = #{isApproved}
        AND m.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countPopularAllMuseumsWithStatus(String search, Boolean isApproved);

    @Select("""
        SELECT COUNT(DISTINCT m.museum_id)
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        WHERE ui.is_verified = true
        AND m.museum_category_id = #{museumCategoryId}::UUID
        AND m.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllPopularMuseumsByCategory(String search, UUID museumCategoryId);

    @Select("""
        SELECT COUNT(DISTINCT m.museum_id)
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        WHERE ui.is_verified = true
        AND m.museum_category_id = #{museumCategoryId}::UUID
        AND m.is_approved = #{isApproved}
        AND m.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllPopularMuseumsByCategoryAndStatus(String search, UUID museumCategoryId, Boolean isApproved);

    // Count methods for visitor (same as non-visitor since count doesn't depend on favorites)
    @Select("""
        SELECT COUNT(DISTINCT museum_owners.museum_id)
        FROM museum_owners
        INNER JOIN user_info ON user_info.user_id = museum_owners.user_id
        WHERE user_info.is_verified = true 
        AND museum_owners.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllMuseumsForVisitor(UUID visitorId, String search);

    @Select("""
        SELECT COUNT(DISTINCT museum_owners.museum_id)
        FROM museum_owners
        INNER JOIN user_info ON user_info.user_id = museum_owners.user_id
        WHERE user_info.is_verified = true 
        AND museum_owners.is_approved = #{isApproved}
        AND museum_owners.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllMuseumsWithStatusForVisitor(UUID visitorId, String search, Boolean isApproved);

    @Select("""
        SELECT COUNT(DISTINCT museum_owners.museum_id)
        FROM museum_owners
        INNER JOIN user_info ON user_info.user_id = museum_owners.user_id
        WHERE user_info.is_verified = true 
        AND museum_owners.museum_category_id = #{museumCategoryId}::UUID
        AND museum_owners.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllMuseumsByCategoryForVisitor(UUID visitorId, String search, UUID museumCategoryId);

    @Select("""
        SELECT COUNT(DISTINCT museum_owners.museum_id)
        FROM museum_owners
        INNER JOIN user_info ON user_info.user_id = museum_owners.user_id
        WHERE user_info.is_verified = true 
        AND museum_owners.museum_category_id = #{museumCategoryId}::UUID
        AND museum_owners.is_approved = #{isApproved}
        AND museum_owners.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllMuseumsByCategoryAndStatusForVisitor(UUID visitorId, String search, UUID museumCategoryId, Boolean isApproved);

    @Select("""
        SELECT COUNT(DISTINCT m.museum_id)
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        WHERE ui.is_verified = true
        AND m.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllPopularMuseumsForVisitor(UUID visitorId, String search);

    @Select("""
        SELECT COUNT(DISTINCT m.museum_id)
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        WHERE ui.is_verified = true
        AND m.is_approved = #{isApproved}
        AND m.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countPopularAllMuseumsWithStatusForVisitor(UUID visitorId, String search, Boolean isApproved);

    @Select("""
        SELECT COUNT(DISTINCT m.museum_id)
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        WHERE ui.is_verified = true
        AND m.museum_category_id = #{museumCategoryId}::UUID
        AND m.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllPopularMuseumsByCategoryForVisitor(UUID visitorId, String search, UUID museumCategoryId);

    @Select("""
        SELECT COUNT(DISTINCT m.museum_id)
        FROM museum_owners m
        INNER JOIN user_info ui ON ui.user_id = m.user_id
        WHERE ui.is_verified = true
        AND m.museum_category_id = #{museumCategoryId}::UUID
        AND m.is_approved = #{isApproved}
        AND m.name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllPopularMuseumsByCategoryAndStatusForVisitor(UUID visitorId, String search, UUID museumCategoryId, Boolean isApproved);


    @ResultMap("museumMapper")
    @Select("""
        SELECT * FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id 
        WHERE is_approved = false AND is_verified = true AND museum_category_id = #{museumCategoryId}::UUID
        ORDER BY museum_owners.created_at DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> getAllRequestMuseumsByCategoryId(UUID museumCategoryId, Integer page, Integer size);

    @Select("""
        SELECT COUNT(*) FROM museum_owners INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_approved = false AND is_verified = true
    """)
    Integer countAllRequestMuseums();

    @Update("""
        UPDATE museum_owners SET is_approved = true WHERE museum_id = #{museumId}::UUID;
    """)
    void udpateIsApprovedStatus(UUID museumId);

    @Results(id = "MuseumWithDistanceMapper", value = {
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "address", column = "address"),
            @Result(property = "logoLink", column = "logo_link"),
            @Result(property = "isFavorite", column = "is_favorite"),
            @Result(property = "lat", column = "lat"),
            @Result(property = "lng", column = "lng"),
            @Result(property = "distanceKm", column = "distance_km")
    })
    @Select("""
        SELECT m.museum_id, m.name, m.address, m.logo_link,
               m.lat, m.lng, false AS is_favorite,
               ROUND(CAST((6371 * acos(
                   cos(radians(#{lat})) * cos(radians(m.lat)) *
                   cos(radians(m.lng) - radians(#{lng})) +
                   sin(radians(#{lat})) * sin(radians(m.lat))
               )) AS NUMERIC), 2) AS distance_km
        FROM museum_owners m
        WHERE m.is_approved = true
          -- More generous bounding box (add small buffer)
          AND m.lat BETWEEN #{lat} - (#{distance}/110.0) AND #{lat} + (#{distance}/110.0)
          AND m.lng BETWEEN #{lng} - (#{distance}/(110.0*cos(radians(#{lat}))))
                              AND #{lng} + (#{distance}/(110.0*cos(radians(#{lat}))))
          -- Accurate distance filter with small buffer for precision
          AND (6371 * acos(
                   cos(radians(#{lat})) * cos(radians(m.lat)) * 
                   cos(radians(m.lng) - radians(#{lng})) + 
                   sin(radians(#{lat})) * sin(radians(m.lat))
               )) <= #{distance} + 0.01
        ORDER BY distance_km ASC
        LIMIT 50
    """)
    List<MuseumWithDistanceResponse> findNearbyMuseumsOptimized(
            @Param("lat") BigDecimal latitude,
            @Param("lng") BigDecimal longitude,
            @Param("distance") Integer distanceKm
    );

    @ResultMap("MuseumWithDistanceMapper")
    @Select("""
        SELECT m.museum_id, m.name, m.address, m.logo_link,
               m.lat, m.lng,
               ROUND(CAST((6371 * acos(
                   cos(radians(#{lat})) * cos(radians(m.lat)) *
                   cos(radians(m.lng) - radians(#{lng})) +
                   sin(radians(#{lat})) * sin(radians(m.lat))
               )) AS NUMERIC), 2) AS distance_km,
        CASE WHEN f.visitor_id IS NOT NULL THEN true ELSE false END AS is_favorite
        FROM museum_owners m
        LEFT JOIN favorites f ON m.museum_id = f.museum_id
            AND f.visitor_id = #{visitorId}::UUID
        WHERE m.is_approved = true
          -- More generous bounding box (add small buffer)
          AND m.lat BETWEEN #{lat} - (#{distance}/110.0) AND #{lat} + (#{distance}/110.0)
          AND m.lng BETWEEN #{lng} - (#{distance}/(110.0*cos(radians(#{lat}))))
                              AND #{lng} + (#{distance}/(110.0*cos(radians(#{lat}))))
          -- Accurate distance filter with small buffer for precision
          AND (6371 * acos(
                   cos(radians(#{lat})) * cos(radians(m.lat)) * 
                   cos(radians(m.lng) - radians(#{lng})) + 
                   sin(radians(#{lat})) * sin(radians(m.lat))
               )) <= #{distance} + 0.01
        ORDER BY distance_km ASC
        LIMIT 50
    """)
    List<MuseumWithDistanceResponse> findNearbyMuseumsOptimizedForVisitor(
            UUID visitorId,
            @Param("lat") BigDecimal latitude,
            @Param("lng") BigDecimal longitude,
            @Param("distance") Integer distanceKm
    );

    @ResultMap("museumMapper")
    @Select("""
        SELECT m.*, COUNT(b.booking_id) AS booking_count
        FROM museum_owners m
        LEFT JOIN bookings b ON m.museum_id = b.museum_id
        WHERE m.is_approved = true
        GROUP BY m.museum_id
        ORDER BY booking_count DESC
        OFFSET (#{page}-1)* #{size} LIMIT #{size};
    """)
    List<MuseumOwner> findAllMuseumOrderbyPopular(Integer page, Integer size);

    @Select("""
        SELECT COUNT(*) FROM museum_owners WHERE is_approved = true
    """)
    Integer countAllMuseumOrderbyPopular();
}
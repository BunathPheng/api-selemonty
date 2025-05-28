package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumWithDistanceResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Mapper
public interface MuseumManagementForAdminRepository {
    @Results(id = "shortMuseumMapper", value = {
            @Result(property = "museumId", column = "museum_id", javaType = UUID.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "museumCategory", column = "museum_category_id", javaType = UUID.class, jdbcType = JdbcType.VARCHAR,
                    many = @Many(select = "org.hrd.finalprojectmuseum.repository.MuseumOwnerRepository.findMuseumCategoryById")),
            @Result(property = "name", column = "name"),
            @Result(property = "contactNumber", column = "contact_number"),
            @Result(property = "logoLink", column = "logo_link"),
            @Result(property = "lng", column = "lat"),
            @Result(property = "lat", column = "lat"),
            @Result(property = "description", column = "description"),
            @Result(property = "isApproved", column = "is_approved"),
    })
    @Select("""
        SELECT museum_id, museum_category_id, name, contact_number, logo_link, lng, lat,
               description, is_approved FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_approved = false AND is_verified = true
        offset (#{page}-1)* #{size} limit #{size};
    """)
    List<MuseumShortInfo> getAllRequestMuseums(Integer page, Integer size);

    @Select("""
        SELECT museum_id, museum_category_id, name, contact_number, logo_link, lng, lat,
               description, is_approved FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_approved = false AND is_verified = true AND museum_category_id = #{museumCategoryId}::UUID
        offset (#{page}-1)* #{size} limit #{size};
    """)
    List<MuseumShortInfo> getAllRequestMuseumsByCategoryId(UUID museumCategoryId, Integer page, Integer size);

    @Select("""
        SELECT COUNT(*) FROM museum_owners INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_approved = false AND is_verified = true
    """)
    Integer countAllRequestMuseums();

    @Update("""
        UPDATE museum_owners SET is_approved = true WHERE museum_id = #{museumId}::UUID;
    """)
    void udpateIsApprovedStatus(UUID museumId);

    @ResultMap("shortMuseumMapper")
    @Select("""
        SELECT museum_id, museum_category_id, name, contact_number, logo_link, lng, lat,
               description, is_approved FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id
        WHERE is_verified = true AND name ILIKE CONCAT('%', #{search}, '%')
        offset (#{page}-1)* #{size} limit #{size};
    """)
    List<MuseumShortInfo> getAllMuseums(String search, Integer page, Integer size);

    @ResultMap("shortMuseumMapper")
    @Select("""
        SELECT museum_id, museum_category_id, name, contact_number, logo_link, lng, lat,
               description, is_approved FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id
        WHERE is_verified = true AND museum_category_id = #{museumCategoryId}::UUID AND name ILIKE CONCAT('%', #{search}, '%')
        offset (#{page}-1)* #{size} limit #{size};
    """)
    List<MuseumShortInfo> getAllMuseumsByCategoryId(String search, UUID museumCategoryId, Integer page, Integer size);

    @Select("""
        SELECT COUNT(*) FROM museum_owners INNER JOIN user_info on user_info.user_id = museum_owners.user_id
        WHERE is_verified = true AND name ILIKE CONCAT('%', #{search}, '%')
    """)
    Integer countAllMuseums(String search);

    @ResultMap("shortMuseumMapper")
    @Select("""
        SELECT museum_id, museum_category_id, name, contact_number, logo_link, lng, lat,
               description, is_approved FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_approved = true AND is_verified = true
        offset (#{page}-1)* #{size} limit #{size};
    """)
    List<MuseumShortInfo> getAllApprovedMuseums(Integer page, Integer size);

    @ResultMap("shortMuseumMapper")
    @Select("""
        SELECT museum_id, museum_category_id, name, contact_number, logo_link, lng, lat,
               description, is_approved FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_approved = true AND is_verified = true AND museum_category_id = #{museumCategoryId}::UUID
        offset (#{page}-1)* #{size} limit #{size};
    """)
    List<MuseumShortInfo> getAllApprovedMuseumsByCategoryId(UUID museumCategoryId, Integer page, Integer size);

    @Select("""
        SELECT COUNT(*) FROM museum_owners INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_approved = true AND is_verified = true
    """)
    Integer countAllApprovedMuseums();

    @Select("""
        SELECT COUNT(*) FROM museum_owners INNER JOIN user_info on user_info.user_id = museum_owners.user_id
        WHERE is_verified = true AND name ILIKE CONCAT('%', #{search}, '%') AND museum_category_id = #{museumCategoryId}::UUID
    """)
    Integer countAllMuseumsByCategory(String search, UUID museumCategoryId);

    @Results(id = "MuseumWithDistanceMapper", value = {
            @Result(property = "museumId", column = "museum_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "address", column = "address"),
            @Result(property = "imageUrl", column = "image_url"),
            @Result(property = "lat", column = "lat"),
            @Result(property = "lng", column = "lng"),
            @Result(property = "distanceKm", column = "distance_km")
    })
    @Select("""
        SELECT m.museum_id, m.name, m.address, m.banner_link,
               m.lat, m.lng,
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
}

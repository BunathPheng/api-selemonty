package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;

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
            @Result(property = "description", column = "description"),
            @Result(property = "isApproved", column = "is_approved"),
    })
    @Select("""
        SELECT museum_id, museum_category_id, name, contact_number, logo_link,
               description, is_approved FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_approved = false AND is_verified = true
        offset (#{page}-1)* #{size} limit #{size};
    """)
    List<MuseumShortInfo> getAllRequestMuseums(Integer page, Integer size);

    @Select("""
        SELECT museum_id, museum_category_id, name, contact_number, logo_link,
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
        SELECT museum_id, museum_category_id, name, contact_number, logo_link,
               description, is_approved FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_verified = true
        offset (#{page}-1)* #{size} limit #{size};
    """)
    List<MuseumShortInfo> getAllMuseums(Integer page, Integer size);

    @ResultMap("shortMuseumMapper")
    @Select("""
        SELECT museum_id, museum_category_id, name, contact_number, logo_link,
               description, is_approved FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_verified = true AND museum_category_id = #{museumCategoryId}::UUID
        offset (#{page}-1)* #{size} limit #{size};
    """)
    List<MuseumShortInfo> getAllMuseumsByCategoryId(UUID museumCategoryId, Integer page, Integer size);

    @Select("""
        SELECT COUNT(*) FROM museum_owners INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_verified = true
    """)
    Integer countAllMuseums();

    @ResultMap("shortMuseumMapper")
    @Select("""
        SELECT museum_id, museum_category_id, name, contact_number, logo_link,
               description, is_approved FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_approved = true AND is_verified = true
        offset (#{page}-1)* #{size} limit #{size};
    """)
    List<MuseumShortInfo> getAllApprovedMuseums(Integer page, Integer size);

    @ResultMap("shortMuseumMapper")
    @Select("""
        SELECT museum_id, museum_category_id, name, contact_number, logo_link,
               description, is_approved FROM museum_owners
        INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_approved = true AND is_verified = true AND museum_category_id = #{museumCategoryId}::UUID
        offset (#{page}-1)* #{size} limit #{size};
    """)
    List<MuseumShortInfo> getAllApprovedMuseumsByCategoryId(UUID museumCategoryId, Integer page, Integer size);

    @Select("""
        SELECT COUNT(*) FROM museum_owners INNER JOIN user_info on user_info.user_id = museum_owners.user_id WHERE is_approved = true AND is_verified = true
    """)
    Integer countAllApprovedMuseums();
}

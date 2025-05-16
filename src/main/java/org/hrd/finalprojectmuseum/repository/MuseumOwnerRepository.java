package org.hrd.finalprojectmuseum.repository;

import com.alibaba.fastjson2.JSONObject;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumOwnerRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumCategory;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface MuseumOwnerRepository {
    @Results(id = "museumMapper", value = {
            @Result(property = "museumId", column = "museum_id", javaType = UUID.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "appUserRegister", column = "user_id",
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.AppUserRepository.getUserById")),
            @Result(property = "museumCategory", column = "museum_category_id", javaType = UUID.class, jdbcType = JdbcType.VARCHAR,
                    many = @Many(select = "findMuseumCategoryById")),
            @Result(property = "name", column = "name"),
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
    })
    @Select("""
        SELECT * FROM museum_owners where user_id =#{userId}::UUID
    """)
    MuseumOwner findMuseumOwnerByUserId(UUID userId);

    @Update("""
        UPDATE museum_owners SET museum_category_id = #{museum.museumCategoryId}::UUID, name = #{museum.name}, contact_number = #{museum.contactNumber}, lat = #{museum.lat},
                                 lng = #{museum.lng}, logo_link = #{museum.logoLink}, banner_link = #{museum.bannerLink},
                                 landscape_links = #{museum.landscapeLink}::JSONB, description = #{museum.description},
                                 updated_at = #{updatedAt} WHERE museum_id = #{museumId}::UUID;
    """)
    void modifyMuseumOwnerById(UUID museumId, @Param("museum") MuseumOwnerRequest museumOwnerRequest, LocalDateTime updatedAt);

    @Delete("""
        DELETE FROM museum_owners where museum_id =#{museumId}::UUID
    """)
    void removeMuseumOwnerByMuseumId(UUID museumId);

    @Select("""
        UPDATE museum_owners SET landscape_links = #{existLandscape}::JSONB WHERE museum_id = #{museumId}::UUID RETURNING landscape_links
    """)
    JSONObject modifyLandscapeByMuseumId(UUID museumId, JSONObject existLandscape);

    @Result(property = "museumCategoryId", column = "museum_category_id")
    @Select("""
        SELECT museum_category_id, name FROM museum_categories;
    """)
    List<MuseumCategory> getMuseumCategories();

    @Result(property = "museumCategoryId", column = "museum_category_id")
    @Select("""
        SELECT museum_category_id, name FROM museum_categories WHERE museum_category_id = #{museumCategoryId}::UUID;
    """)
    MuseumCategory findMuseumCategoryById(UUID museumCategoryId);
}

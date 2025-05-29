package org.hrd.finalprojectmuseum.repository;

import com.alibaba.fastjson2.JSONObject;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.dto.request.PaymentAccountRequest;
import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumOwnerRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumCategory;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface ProfileRepository {

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

    @Select("""
        UPDATE museum_owners SET client_id = #{museum.}, client_secret = #{museum.clientSecret},
        account_name = #{museum.accountName}, parent_account_no = #{museum.parentAccountNo},
        updated_at = #{updatedAt} WHERE user_id = #{userId}::UUID RETURNING *;
    """)
    MuseumOwner updateMuseumPaymentByUserId(UUID userId, @Param("museum") PaymentAccountRequest paymentAccountRequest);

    // For admin

    @Results(id = "adminMapper", value = {
            @Result(property = "adminId", column = "admin_id"),
            @Result(property = "appUserRegister", column = "user_id",
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.AppUserRepository.getUserById")),
            @Result(property = "name", column = "name"),
            @Result(property = "profileImage", column = "profile_image"),
            @Result(property = "createdAt", column = "created_at"),
    })
    @Select("""
        SELECT * FROM admin WHERE user_id = #{userId}::UUID;
    """)
    Admin findAdminByUserId(UUID userId);

    @ResultMap("adminMapper")
    @Select("""
        UPDATE admin SET name = #{admin.name}, profile_image_link = #{admin.profileImageLink} WHERE admin_id = #{adminId}::UUID RETURNING *
    """)
    Admin modifyAdminByAdminId(UUID adminId, @Param("admin") AdminRequest adminRequest);

    // visitor

    @Results(id = "visitorMapper", value = {
            @Result(property = "visitorId", column = "visitor_id"),
            @Result(property = "appUserRegister", column = "user_id",
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.AppUserRepository.getUserById")),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "contactNumber", column = "contact_number"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "dob", column = "dob"),
            @Result(property = "profileImageLink", column = "profile_image_link"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
        SELECT * FROM visitors WHERE user_id = #{userId}::UUID
    """)
    Visitor findVisitor(UUID userId);

    @ResultMap("visitorMapper")
    @Select("""
       UPDATE visitors SET full_name = #{visitor.fullName}, contact_number = #{visitor.contactNumber}, gender = #{visitor.gender},
                           dob = #{visitor.dob}, profile_image_link = #{visitor.profileImageLink}, updated_at = #{updatedAt}  WHERE user_id = #{userId}::uuid
                            RETURNING *;
    """)
    Visitor modifyVisitorByVisitorId(UUID userId, @Param("visitor") VisitorRequest visitorRequest, LocalDateTime updatedAt);

    @ResultMap("visitorMapper")
    @Select("""
        SELECT * FROM visitors WHERE visitor_id = #{visitorId}::UUID
    """)
    Visitor findVisitorById(UUID visitorId);
}

package org.hrd.finalprojectmuseum.repository;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumAboutRequest;
import org.hrd.finalprojectmuseum.model.dto.request.PaymentAccountRequest;
import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumOwnerRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.entity.PaymentCredential;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper
@Repository
public interface ProfileRepository {

    @Results(id = "museumMapper", value = {
            @Result(property = "museumId", column = "museum_id", javaType = UUID.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "appUserRegister", column = "user_id",
                    one = @One(select = "org.hrd.finalprojectmuseum.repository.AppUserRepository.getUserById")),
            @Result(property = "museumCategory", column = "museum_category_id", javaType = UUID.class, jdbcType = JdbcType.VARCHAR,
                    many = @Many(select = "org.hrd.finalprojectmuseum.repository.MuseumRepository.findMuseumCategoryById")),
            @Result(property = "name", column = "name"),
            @Result(property = "contactNumber", column = "contact_number"),
            @Result(property = "address", column = "address"),
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
        SELECT m.* FROM museum_owners m
        where user_id =#{userId}::UUID
    """)
    MuseumOwner findMuseumOwnerByUserId(UUID userId);

    @Select("""
        SELECT COUNT(museum_zone_id) FROM museum_zones WHERE museum_id = #{museumId}::UUID;
    """)
    Integer totalZoneByMuseumId(UUID museumId);

    @Select("""
        SELECT COUNT(artifact_id) FROM museum_zones z INNER JOIN artifacts a ON z.museum_zone_id = a.museum_zone_id
        WHERE z.museum_id = #{museumId}::UUID;
    """)
    Integer totalArtifactByMuseumId(UUID museumId);

    @Update("""
        UPDATE museum_owners SET museum_category_id = #{museum.museumCategoryId}::UUID, name = #{museum.name}, contact_number = #{museum.contactNumber},
                                 address = #{museum.address}, lat = #{museum.lat}, lng = #{museum.lng}, logo_link = #{museum.logoLink},
                                 banner_link = #{museum.bannerLink}, landscape_links = #{museum.landscapeLink}::JSONB,
                                 description = #{museum.description}, updated_at = #{updatedAt}
                             WHERE museum_id = #{museumId}::UUID;
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

    @ResultMap("paymentCredential")
    @Select("""
        UPDATE museum_owners SET client_id = #{museum.clientId}, client_secret = #{museum.clientSecret},
        account_name = #{museum.accountName}, parent_account_no = #{museum.parentAccountNo},
        updated_at = #{updatedAt} WHERE user_id = #{userId}::UUID RETURNING *;
    """)
    PaymentCredential updateMuseumPaymentByUserId(UUID userId, @Param("museum") PaymentAccountRequest paymentAccountRequest, LocalDateTime updatedAt);

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

    @Select("""
        SELECT museum_id FROM museum_owners WHERE user_id = #{userId}::UUID
    """)
    UUID getMuseumIdByUserId(UUID userId);

    @Results(id = "paymentCredential", value = {
            @Result(property = "clientId", column = "client_id"),
            @Result(property = "clientSecret", column = "client_secret"),
            @Result(property = "accountName", column = "account_name"),
            @Result(property = "parentAccountNumber", column = "parent_account_no")
    })
    @Select("""
        SELECT client_id, client_secret, account_name, parent_account_no FROM museum_owners WHERE museum_id = #{museumId}::UUID;
    """)
    PaymentCredential retrieveMuseumPaymentCredential(UUID museumId);

    @ResultMap("museumMapper")
    @Select("""
        UPDATE museum_owners SET name = #{museum.name}, museum_category_id = #{museum.categoryId}::UUID,
        description = #{museum.description} WHERE museum_id = #{museumId}::UUID RETURNING *;
    """)
    MuseumOwner modifyMuseumAboutDetailByMuseumId(UUID museumId, @Param("museum") MuseumAboutRequest museumAboutRequest);

    @ResultMap("museumMapper")
    @Select("""
        UPDATE museum_owners SET contact_number = #{contactNumber}
        WHERE museum_id = #{museumId}::UUID RETURNING *;
    """)
    MuseumOwner modifyMuseumContactByMuseumId(UUID museumId, String contactNumber);

    @ResultMap("museumMapper")
    @Select("""
        UPDATE museum_owners SET landscape_links = #{landscapeLink}::JSONB
        WHERE museum_id = #{museumId}::UUID RETURNING *;
    """)
    MuseumOwner modifyMuseumLandscapeByMuseumId(UUID museumId, JSONObject landscapeLink);

    @ResultMap("museumMapper")
    @Select("""
        UPDATE museum_owners SET banner_link = #{bannerLink}
        WHERE museum_id = #{museumId}::UUID RETURNING *;
    """)
    MuseumOwner modifyMuseumBannerByMuseumId(UUID museumId, String bannerLink);

    @ResultMap("museumMapper")
    @Select("""
        UPDATE museum_owners SET logo_link = #{logoLink}
        WHERE museum_id = #{museumId}::UUID RETURNING *;
    """)
    MuseumOwner modifyMuseumLogoByMuseumId(UUID museumId, String logoLink);
}

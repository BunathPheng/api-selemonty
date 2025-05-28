package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.entity.AppUser;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.enums.Role;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface AppUserRepository {

    @Select("""
            SELECT * from user_info
            WHERE email = #{email}
            """)
    @Results(id = "userMapper", value = {
            @Result(property = "userId", column = "user_id", javaType = UUID.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "role", column = "role"),
            @Result(property = "isVerified", column = "is_verified"),
            @Result(property = "password", column = "password"),
//            @Result(property = "isApprove", column = "is_approve"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "createdAt", column = "created_at"),
    })
    Optional<AppUser> getUserByEmail(String email);

    @Select("""
    SELECT * FROM user_info
    WHERE user_id = #{appUserId}::UUID
    """)
    @Results(id = "userRegisterMapper", value = {
            @Result(property = "userId", column = "user_id", javaType = UUID.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "email", column = "email"),
            @Result(property = "password", column = "password"),
            @Result(property = "role", column = "role"),
            @Result(property = "isVerified", column = "is_verified"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    AppUserRegister getUserById(UUID appUserId);

    @ResultMap(value = "userRegisterMapper")
    @Select("""
            SELECT * from user_info
            WHERE email = #{email}
            """)
    AppUserRegister findUserByEmail(String email);


    @Select("""
            INSERT INTO user_info(email, password, role, is_verified)
            VALUES(#{email}, #{password}, #{role}, #{isVerified})
            RETURNING *
            """)
    @ResultMap("userRegisterMapper")
    AppUserRegister registerUser(String email, String password, Role role, Boolean isVerified);

    @Update("""
            UPDATE user_info
            SET is_verified = true
            WHERE email = #{email}
            """)
    void verifyEmailWithOpt(String email);

    @Update("""
     UPDATE user_info SET password = #{password} WHERE email = #{email}
    """)
    void updatePassword(AppUser user);

    @Insert("""
        INSERT INTO visitors(user_id, full_name, profile_image_link) VALUES(#{userId}::uuid, #{fullName}, #{profileImageLink})
    """)
    void storeVisitor(UUID userId, String fullName, String profileImageLink);

    @Insert("""
        INSERT INTO museum_owners(user_id, name, lat, lng, logo_link, description)
        VALUES (#{userId}::uuid, #{name}, #{lat}, #{lng}, #{logoLink}, #{description})
    """)
    void storeMeseumOwner(UUID userId, String name, String logoLink, BigDecimal lat, BigDecimal lng, String description);

    @Delete("""
        DELETE FROM user_info WHERE user_id = #{userId}::UUID
    """)
    void deleteUser(UUID userId);

    @Update("""
        UPDATE user_info SET password = #{newPassword} WHERE user_id = #{userId}::UUID
    """)
    void updatePasswordByUserId(UUID userId, String newPassword);
}

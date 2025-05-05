package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.dto.request.RegisterRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUser;

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
//            @Result(property = "isApprove", column = "is_approve"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "createdAt", column = "created_at"),
    })
    Optional<AppUser> getUserByEmail(String email);

    @ResultMap(value = "userMapper")
    @Select("""
            SELECT * from user_info
            WHERE email = #{email}
            """)
    AppUser findUserByEmail(String email);
    @Select("""
            SELECT * from user_info
            WHERE user_id = #{appUserId}::UUID
            """)
    @ResultMap("userMapper")
    AppUser getUserById(UUID appUserId);


    @Select("""
            INSERT INTO user_info(email, password, role)
            VALUES(#{user.email}, #{user.password}, #{user.role})
            RETURNING *
            """)
    @ResultMap("userMapper")
    AppUser registerUser(@Param("user") RegisterRequest registerRequest);

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
}

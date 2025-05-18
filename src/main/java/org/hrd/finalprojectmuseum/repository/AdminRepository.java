package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;

import java.util.UUID;

@Mapper
public interface AdminRepository {
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
}

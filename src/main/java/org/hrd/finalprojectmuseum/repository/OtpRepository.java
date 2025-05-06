package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.entity.Otps;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper
public interface OtpRepository {
    @Results(id = "otpMapper", value = {
            @Result(property = "otpId", column = "otp_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "otpCode", column = "otp_code"),
            @Result(property = "expiredDate", column = "expired_date"),
            @Result(property = "createdAt", column = "created_at"),
    })
    @Insert("""
        INSERT INTO otps (user_id, otp_code, expired_date)
        VALUES (#{userId}::uuid, #{otp}, #{expiredDate})
    """)
    void saveOpt(UUID userId, LocalDateTime expiredDate, String otp);

    @Delete("""
        DELETE FROM otps WHERE user_id = #{userId}::uuid
    """)
    void removeOptByUserId(UUID userId);

    @Select("""
        SELECT * FROM otps WHERE user_id = #{userId}::uuid
    """)
    Otps getOptByUserId(UUID userId);

    @Select("""
        SELECT expired_date FROM otps WHERE user_id = #{userId}::uuid
    """)
    LocalDateTime getExpirationByUserId(UUID userId);
}

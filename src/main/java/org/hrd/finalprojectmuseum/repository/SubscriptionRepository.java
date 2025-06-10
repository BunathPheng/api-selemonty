package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.entity.Subscriptions;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository {
    @Select("SELECT * FROM subscriptions WHERE user_id = #{userId}::UUID")
    List<Subscriptions> findByUserId(@Param("userId") UUID userId);

    @Select("SELECT * FROM subscriptions WHERE subscription_code = #{subscriptionCode}")
    Subscriptions findBySubscriptionCode(@Param("subscriptionCode") String subscriptionCode);

    @Select("SELECT subscription_code FROM subscriptions WHERE user_id = #{userId}::UUID")
    List<String> findSubscriptionCodesByUserId(@Param("userId") UUID userId);

    @Select("SELECT subscription_code FROM subscriptions")
    List<String> findAllSubscriptionCodes();

    @Select("SELECT s.*, u.email, u.role FROM subscriptions s " +
            "LEFT JOIN user_info u ON s.user_id = u.user_id WHERE s.user_id = #{userId}::UUID")
    @Results({
            @Result(property = "subscriptionId", column = "subscription_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "subscriptionCode", column = "subscription_code"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "user.userId", column = "user_id"),
            @Result(property = "user.email", column = "email"),
            @Result(property = "user.role", column = "role")
    })
    List<Subscriptions> findByUserIdWithUser(@Param("userId") UUID userId);

    @Insert("INSERT INTO subscriptions (user_id, subscription_code, created_at, updated_at) " +
            "VALUES (#{userId}, #{subscriptionCode}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "subscriptionId")
    int insert(Subscriptions Subscriptions);

    @Update("UPDATE subscriptions SET subscription_code = #{subscriptionCode}, updated_at = #{updatedAt} " +
            "WHERE subscription_id = #{subscriptionId}")
    int update(Subscriptions Subscriptions);

    @Delete("DELETE FROM subscriptions WHERE subscription_id = #{subscriptionId}")
    int deleteById(@Param("subscriptionId") UUID subscriptionId);

    @Delete("DELETE FROM subscriptions WHERE user_id = #{userId} AND subscription_code = #{subscriptionCode}")
    int deleteByUserIdAndSubscriptionCode(@Param("userId") UUID userId, @Param("subscriptionCode") String subscriptionCode);
}

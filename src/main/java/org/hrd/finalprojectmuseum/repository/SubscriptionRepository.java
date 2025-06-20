package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.entity.NotificationMessage;
import org.hrd.finalprojectmuseum.model.entity.Subscriptions;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
@Repository
public interface SubscriptionRepository {

    @ResultMap("subscriptionMapper")
    @Select("SELECT * FROM subscriptions WHERE user_id = #{userId}::UUID")
    List<Subscriptions> findByUserId(@Param("userId") UUID userId);

    @Results(id = "subscriptionMapper", value = {
            @Result(property = "subscriptionId", column = "subscription_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "subscriptionCode", column = "subscription_code"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
    })
    @Select("SELECT * FROM subscriptions WHERE subscription_code = #{subscriptionCode}")
    Subscriptions findBySubscriptionCode(@Param("subscriptionCode") String subscriptionCode);

    @Select("SELECT subscription_code FROM subscriptions WHERE user_id = #{userId}::UUID")
    List<String> findSubscriptionCodesByUserId(@Param("userId") UUID userId);

    @Select("SELECT subscription_code FROM subscriptions")
    List<String> findAllSubscriptionCodes();

    @ResultMap("subscriptionMapper")
    @Select("SELECT s.* FROM subscriptions s " +
            "WHERE s.user_id = #{userId}::UUID")
    List<Subscriptions> findByUserIdWithUser(@Param("userId") UUID userId);

    @Insert("INSERT INTO notification_message (subscription_id, title, message, is_read, created_at) " +
            "VALUES (#{notification.subscriptionId}, #{notification.title}, #{notification.message}, #{notification.isRead}, #{notification.createdAt})")
    void insert(@Param("notification") NotificationMessage notificationMessage);

    @Update("UPDATE subscriptions SET subscription_code = #{subscriptionCode}, updated_at = #{updatedAt} " +
            "WHERE subscription_id = #{subscriptionId}")
    int update(Subscriptions Subscriptions);

    @Delete("DELETE FROM subscriptions WHERE subscription_id = #{subscriptionId}")
    int deleteById(@Param("subscriptionId") UUID subscriptionId);

    @Delete("DELETE FROM subscriptions WHERE user_id = #{userId}::UUID AND subscription_code = #{subscriptionCode}")
    int deleteByUserIdAndSubscriptionCode(@Param("userId") UUID userId, @Param("subscriptionCode") String subscriptionCode);
}

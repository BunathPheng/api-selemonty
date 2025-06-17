package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.entity.NotificationMessage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Mapper
@Repository
public interface NotificationMessageRepository {
    @Select("SELECT * FROM notification_message WHERE notification_id = #{notificationMessageId}::UUID")
    NotificationMessage findById(@Param("notificationMessageId") UUID notificationMessageId);

    @Select("SELECT * FROM notification_message WHERE subscription_id = #{subscriptionId}::UUID")
    List<NotificationMessage> findBySubscriptionId(@Param("subscriptionId") UUID subscriptionId);

    @Select("SELECT nm.* FROM notification_message nm " +
            "JOIN subscriptions s ON nm.subscription_id = s.subscription_id " +
            "WHERE s.user_id = #{userId}::UUID ORDER BY nm.created_at DESC")
    List<NotificationMessage> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    @Select("SELECT nm.* FROM notification_message nm " +
            "JOIN subscriptions s ON nm.subscription_id = s.subscription_id " +
            "WHERE s.user_id = #{userId}::UUID AND nm.is_read = false ORDER BY nm.created_at DESC")
    List<NotificationMessage> findUnreadByUserId(@Param("userId") UUID userId);

    @Select("SELECT nm.*, s.subscription_code, u.email FROM notification_message nm " +
            "LEFT JOIN subscriptions s ON nm.subscription_id = s.subscription_id " +
            "LEFT JOIN user_info u ON s.user_id = u.user_id " +
            "WHERE s.user_id = #{userId}::UUID ORDER BY nm.created_at DESC")
    @Results({
            @Result(property = "notificationMessageId", column = "notification_message_id"),
            @Result(property = "subscriptionId", column = "subscription_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "message", column = "message"),
            @Result(property = "isRead", column = "is_read"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "subscription.subscriptionId", column = "subscription_id"),
            @Result(property = "subscription.subscriptionCode", column = "subscription_code"),
            @Result(property = "subscription.user.email", column = "email")
    })
    List<NotificationMessage> findByUserIdWithDetails(@Param("userId") UUID userId);

    @Select("SELECT * FROM notification_message WHERE is_read = #{isRead}")
    List<NotificationMessage> findByIsRead(@Param("isRead") Boolean isRead);

    @Insert("INSERT INTO notification_message (subscription_id, title, message, is_read, created_at) " +
            "VALUES (#{subscriptionId}, #{title}, #{message}, #{isRead}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "notificationMessageId")
    int insert(NotificationMessage notificationMessage);

    @Update("UPDATE notification_message SET is_read = #{isRead} WHERE notification_id = #{notificationMessageId}::UUID")
    int updateReadStatus(@Param("notificationMessageId") UUID notificationMessageId, @Param("isRead") Boolean isRead);

    @Update("UPDATE notification_message SET is_read = true " +
            "WHERE subscription_id IN (SELECT s.subscription_id FROM subscriptions s WHERE s.user_id = #{userId})::UUID")
    int markAllAsReadByUserId(@Param("userId") UUID userId);

    @Delete("DELETE FROM notification_message WHERE notification_id = #{notificationMessageId}::UUID")
    int deleteById(@Param("notificationMessageId") UUID notificationMessageId);
}

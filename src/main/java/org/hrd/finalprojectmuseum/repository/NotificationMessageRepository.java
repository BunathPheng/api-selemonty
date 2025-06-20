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

    @ResultMap("notificationMapper")
    @Select("SELECT nm.* FROM notification_message nm " +
            "JOIN subscriptions s ON nm.subscription_id = s.subscription_id " +
            "WHERE s.user_id = #{userId}::UUID ORDER BY nm.created_at DESC")
    List<NotificationMessage> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    @ResultMap("notificationMapper")
    @Select("SELECT nm.* FROM notification_message nm " +
            "JOIN subscriptions s ON nm.subscription_id = s.subscription_id " +
            "WHERE s.user_id = #{userId}::UUID AND nm.is_read = false ORDER BY nm.created_at DESC")
    List<NotificationMessage> findUnreadByUserId(@Param("userId") UUID userId);

    @Select("SELECT nm.* FROM notification_message nm " +
            "LEFT JOIN subscriptions s ON nm.subscription_id = s.subscription_id " +
            "LEFT JOIN user_info u ON s.user_id = u.user_id " +
            "WHERE s.user_id = #{userId}::UUID ORDER BY nm.created_at DESC")
    @Results(id = "notificationMapper", value = {
            @Result(property = "notificationId", column = "notification_id"),
            @Result(property = "subscriptionId", column = "subscription_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "message", column = "message"),
            @Result(property = "isRead", column = "is_read"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<NotificationMessage> findByUserIdWithDetails(@Param("userId") UUID userId);

    @Select("SELECT * FROM notification_message WHERE is_read = #{isRead}")
    List<NotificationMessage> findByIsRead(@Param("isRead") Boolean isRead);

    @Insert("INSERT INTO notification_message (subscription_id, title, message, is_read, created_at) " +
            "VALUES (#{notification.subscriptionId}::UUID, #{notification.title}, #{notification.message}, #{notification.isRead}, #{notification.createdAt})")
    void insert(@Param("notification") NotificationMessage notificationMessage);

    @Update("UPDATE notification_message SET is_read = #{isRead} WHERE notification_id = #{notificationMessageId}::UUID")
    void updateReadStatus(@Param("notificationMessageId") UUID notificationMessageId, @Param("isRead") Boolean isRead);

    @Update("UPDATE notification_message SET is_read = true " +
            "WHERE subscription_id IN (SELECT s.subscription_id FROM subscriptions s WHERE s.user_id = #{userId}::UUID)")
    void markAllAsReadByUserId(@Param("userId") UUID userId);

    @Delete("DELETE FROM notification_message WHERE notification_id = #{notificationMessageId}::UUID")
    void deleteById(@Param("notificationMessageId") UUID notificationMessageId);

    @Select("""
        SELECT EXISTS(
                SELECT 1
                FROM notification_message nm
                JOIN subscriptions s ON nm.subscription_id = s.subscription_id
                WHERE s.user_id = #{userId}::UUID
                AND nm.notification_id = #{notificationId}::UUID
            )
    """)
    boolean isNotificationBelongToUser(UUID userId, UUID notificationId);
}

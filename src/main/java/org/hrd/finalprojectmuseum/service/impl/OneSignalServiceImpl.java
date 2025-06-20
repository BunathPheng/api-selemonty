package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.hrd.finalprojectmuseum.config.OneSignalConfig;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.NotificationRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.NotificationMessage;
import org.hrd.finalprojectmuseum.model.entity.Subscriptions;
import org.hrd.finalprojectmuseum.repository.AppUserRepository;
import org.hrd.finalprojectmuseum.repository.NotificationMessageRepository;
import org.hrd.finalprojectmuseum.repository.SubscriptionRepository;
import org.hrd.finalprojectmuseum.service.OneSignalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor // This will inject all final fields
public class OneSignalServiceImpl implements OneSignalService {

    private static final Logger logger = LoggerFactory.getLogger(OneSignalServiceImpl.class);

    // All dependencies will be injected by @RequiredArgsConstructor
    private final AppUserRepository appUserRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationMessageRepository notificationMessageRepository;
    private final OneSignalConfig oneSignalConfig;
    private final WebClient webClient; // This will be injected from your WebConfig

    // Remove the manual constructor - @RequiredArgsConstructor handles it

    @Override
    public Subscriptions subscribeUser(UUID userId, String oneSignalPlayerId) {
        AppUserRegister user = appUserRepository.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Check if subscription already exists
        Subscriptions existingSubscription = subscriptionRepository.findBySubscriptionCode(oneSignalPlayerId);
        if (existingSubscription != null) {
            if (existingSubscription.getUserId().equals(userId)) {
                logger.info("User {} already subscribed with this device", userId);
                return existingSubscription;
            } else {
                // Different user with same device - update to new user
                existingSubscription.setUserId(userId);
                existingSubscription.setUpdatedAt(LocalDateTime.now());
                subscriptionRepository.update(existingSubscription);
                logger.info("Updated subscription {} to new user {}", oneSignalPlayerId, userId);
                return existingSubscription;
            }
        }

//        return subscriptionRepository.insert(userId, oneSignalPlayerId);
        return null;
    }

    @Override
    public Mono<String> sendToAllUsers(String title, String message) {
        List<String> allPlayerIds = subscriptionRepository.findAllSubscriptionCodes();

        if (allPlayerIds.isEmpty()) {
            return Mono.just("No subscribers found");
        }

        NotificationRequest request = new NotificationRequest();
        request.setAppId(oneSignalConfig.getAppId());
        request.setIncludePlayerIds(allPlayerIds);

        Map<String, String> contents = new HashMap<>();
        contents.put("en", message);
        request.setContents(contents);

        Map<String, String> headings = new HashMap<>();
        headings.put("en", title);
        request.setHeadings(headings);

        return sendNotification(request)
                .doOnSuccess(response -> {
                    System.out.println(response);
                    saveNotificationToDatabase(title, message, allPlayerIds);
                });
    }

    @Override
    public Mono<String> sendToUser(UUID userId, String title, String message) {
        List<String> playerIds = subscriptionRepository.findSubscriptionCodesByUserId(userId);
        if (playerIds.isEmpty()) {
            return Mono.just("User has no active subscriptions");
        }

        NotificationRequest request = new NotificationRequest();
        request.setAppId(oneSignalConfig.getAppId());
        request.setIncludePlayerIds(playerIds);

        Map<String, String> contents = new HashMap<>();
        contents.put("en", message);
        request.setContents(contents);

        Map<String, String> headings = new HashMap<>();
        headings.put("en", title);
        request.setHeadings(headings);
        return sendNotification(request)
                .doOnSuccess(response -> {
                    saveNotificationToDatabase(title, message, playerIds);
                });
    }

    @Override
    public Mono<String> sendToUsers(List<UUID> userIds, String title, String message) {
        List<String> playerIds = new ArrayList<>();

        for (UUID userId : userIds) {
            playerIds.addAll(subscriptionRepository.findSubscriptionCodesByUserId(userId));
        }

        if (playerIds.isEmpty()) {
            return Mono.just("No active subscriptions found for specified users");
        }

        NotificationRequest request = new NotificationRequest();
        request.setAppId(oneSignalConfig.getAppId());
        request.setIncludePlayerIds(playerIds);

        Map<String, String> contents = new HashMap<>();
        contents.put("en", message);
        request.setContents(contents);

        Map<String, String> headings = new HashMap<>();
        headings.put("en", title);
        request.setHeadings(headings);

        return sendNotification(request)
                .doOnSuccess(response -> saveNotificationToDatabase(title, message, playerIds));
    }

    @Override
    public Mono<String> sendWithCustomData(UUID userId, String title, String message,
                                           Map<String, Object> customData, String url) {
        List<String> playerIds = subscriptionRepository.findSubscriptionCodesByUserId(userId);

        if (playerIds.isEmpty()) {
            return Mono.just("User has no active subscriptions");
        }

        NotificationRequest request = new NotificationRequest();
        request.setAppId(oneSignalConfig.getAppId());
        request.setIncludePlayerIds(playerIds);

        Map<String, String> contents = new HashMap<>();
        contents.put("en", message);
        request.setContents(contents);

        Map<String, String> headings = new HashMap<>();
        headings.put("en", title);
        request.setHeadings(headings);

        if (customData != null) {
            request.setData(customData);
        }

        if (url != null) {
            request.setUrl(url);
        }

        return sendNotification(request)
                .doOnSuccess(response -> saveNotificationToDatabase(title, message, playerIds));
    }

    @Override
    public Mono<String> sendToVerifiedUsers(String title, String message) {
        List<AppUserRegister> verifiedUsers = appUserRepository.findByIsVerified(true);
        List<UUID> userIds = verifiedUsers.stream()
                .map(AppUserRegister::getUserId)
                .toList();

        return sendToUsers(userIds, title, message);
    }

    /**
     * Core method to send notification via OneSignal API
     */
    private Mono<String> sendNotification(NotificationRequest request) {
        return webClient.post() // Use injected WebClient
                .uri(oneSignalConfig.getApiUrl())
                .header("Authorization", "Basic " + oneSignalConfig.getRestApiKey())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> logger.info("Notification sent successfully: {}", response))
                .doOnError(error -> logger.error("Failed to send notification: {}", error.getMessage()));
    }

    /**
     * Save notification to database for tracking
     */
    private void saveNotificationToDatabase(String title, String message, List<String> playerIds) {
        for (String playerId : playerIds) {
            Subscriptions subscription = subscriptionRepository.findBySubscriptionCode(playerId);
            System.out.println("Subscription:"+subscription);
            if (subscription != null) {
                NotificationMessage notificationMessage = NotificationMessage.builder()
                        .subscriptionId(subscription.getSubscriptionId()) // Set the foreign key
                        .title(title)
                        .message(message)
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                notificationMessageRepository.insert(notificationMessage);
            } else {
                System.out.println("Subscription not found for playerId: " + playerId);
            }
        }
    }

    @Override
    public List<NotificationMessage> getUserNotifications(UUID userId) {
        return notificationMessageRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<NotificationMessage> getUserNotificationsWithDetails(UUID userId) {
        return notificationMessageRepository.findByUserIdWithDetails(userId);
    }

    @Override
    public List<NotificationMessage> getUserUnreadNotifications(UUID userId) {
        return notificationMessageRepository.findUnreadByUserId(userId);
    }

    @Override
    public void markNotificationAsRead(UUID notificationId, UUID userId) {
        NotificationMessage notificationMessage = notificationMessageRepository.findById(notificationId);
        if (notificationMessage == null) {
            throw new AppNotFoundException("Notification Id: " + notificationId + " not found");
        }
        if (!notificationMessageRepository.isNotificationBelongToUser(userId, notificationId)) {
            throw new AppBadRequestException("Notification Id: " + notificationId + " is not belong to user");
        }
        notificationMessageRepository.updateReadStatus(notificationId, true);
    }

    @Override
    public void markNotificationAsUnread(UUID notificationId) {
        notificationMessageRepository.updateReadStatus(notificationId, false);
    }

    @Override
    public void markAllUserNotificationsAsRead(UUID userId) {
        notificationMessageRepository.markAllAsReadByUserId(userId);
    }

    @Override
    public NotificationMessage getNotificationById(UUID notificationId) {
        return notificationMessageRepository.findById(notificationId);
    }

    @Override
    public void deleteNotification(UUID notificationId) {
        notificationMessageRepository.deleteById(notificationId);
    }

    @Override
    public void unsubscribeUser(UUID userId, String oneSignalPlayerId) {
        subscriptionRepository.deleteByUserIdAndSubscriptionCode(userId, oneSignalPlayerId);
        logger.info("User {} unsubscribed from device {}", userId, oneSignalPlayerId);
    }

    @Override
    public void unsubscribeUserFromAllDevices(UUID userId) {
        List<Subscriptions> subscriptions = subscriptionRepository.findByUserId(userId);
        for (Subscriptions subscription : subscriptions) {
            subscriptionRepository.deleteById(subscription.getSubscriptionId());
        }
        logger.info("User {} unsubscribed from all devices", userId);
    }

    @Override
    public List<Subscriptions> getUserSubscriptions(UUID userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Override
    public List<Subscriptions> getUserSubscriptionsWithDetails(UUID userId) {
        return subscriptionRepository.findByUserIdWithUser(userId);
    }

    @Override
    public Subscriptions getSubscriptionByPlayerId(String playerId) {
        return subscriptionRepository.findBySubscriptionCode(playerId);
    }

    @Override
    public void updateSubscription(Subscriptions subscription) {
        subscription.setUpdatedAt(LocalDateTime.now());
        subscriptionRepository.update(subscription);
    }

    @Override
    public int getTotalSubscriberCount() {
        return subscriptionRepository.findAllSubscriptionCodes().size();
    }

    @Override
    public int getUserUnreadCount(UUID userId) {
        return getUserUnreadNotifications(userId).size();
    }

    // Additional helper methods you might need
    @Override
    public boolean canUserReceiveNotifications(UUID userId) {
        List<String> playerIds = subscriptionRepository.findSubscriptionCodesByUserId(userId);
        return !playerIds.isEmpty();
    }

    @Override
    public List<String> getUserSubscriptionCodes(UUID userId) {
        return subscriptionRepository.findSubscriptionCodesByUserId(userId);
    }
}
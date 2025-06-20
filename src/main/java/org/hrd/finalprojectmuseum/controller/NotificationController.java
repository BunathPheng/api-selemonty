package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.request.SubscriptionRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.NotificationMessage;
import org.hrd.finalprojectmuseum.model.entity.Subscriptions;
import org.hrd.finalprojectmuseum.service.AppUserService;
import org.hrd.finalprojectmuseum.service.OneSignalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/notification")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final OneSignalService oneSignalService;
    private final AppUserService appUserService;

    @PostMapping("/subscribe-notification")
    public ResponseEntity<ApiResponse<Subscriptions>> addUserToSubscription(
            @RequestBody @Valid SubscriptionRequest subscriptionRequest
    ){
        Subscriptions subscriptions = oneSignalService.subscribeUser(appUserService.getUserId(), subscriptionRequest.getSubscriptionCode());
        ApiResponse<Subscriptions> response = ApiResponse.<Subscriptions>builder()
                .success(true)
                .message("Successfully subscribed")
                .status(HttpStatus.CREATED)
                .payload(subscriptions)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/view/all")
    public ResponseEntity<ApiResponse<List<NotificationMessage>>> getAllNotification(){
        UUID userId = appUserService.getUserId();
        List<NotificationMessage> notificationMessages = oneSignalService.getUserNotifications(userId);
        ApiResponse<List<NotificationMessage>> response = ApiResponse.<List<NotificationMessage>>builder()
                .success(true)
                .message("Notifications has been successfully fetched")
                .status(HttpStatus.OK)
                .payload(notificationMessages)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping()
    public ResponseEntity<ApiResponse<Void>> markNotificationAsRead(){
        UUID userId = appUserService.getUserId();
        oneSignalService.markAllUserNotificationsAsRead(userId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Mark as read successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{notification-id}")
    public ResponseEntity<ApiResponse<NotificationMessage>> markNotificationAsReadById(
            @PathVariable("notification-id") @NotNull(message = "Notification Id is required") UUID notificationId
    ){
        UUID userId = appUserService.getUserId();
        oneSignalService.markNotificationAsRead(notificationId, userId);
        ApiResponse<NotificationMessage> response = ApiResponse.<NotificationMessage>builder()
                .success(true)
                .message("Mark as read successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

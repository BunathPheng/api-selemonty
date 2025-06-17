package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.entity.Subscriptions;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface OneSignalService {
    public Subscriptions subscribeUser(UUID userId, String oneSignalPlayerId);
    public Mono<String> sendToAllUsers(String title, String message);
    public Mono<String> sendToUser(UUID userId, String title, String message);
    public Mono<String> sendToUsers(List<UUID> userIds, String title, String message);
}

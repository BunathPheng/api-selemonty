package org.hrd.finalprojectmuseum.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SubscriptionRequest {
    @NotBlank(message = "Subscription code (Player ID) is required")
    private String subscriptionCode;
}

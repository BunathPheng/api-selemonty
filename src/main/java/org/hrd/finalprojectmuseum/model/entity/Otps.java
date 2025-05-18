package org.hrd.finalprojectmuseum.model.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Otps {
    private UUID otp_id;
    private UUID user_id;
    private LocalDateTime expiredDate;
    private LocalDateTime createdAt;
}

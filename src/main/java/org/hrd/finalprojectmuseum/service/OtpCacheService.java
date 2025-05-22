package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.entity.Otps;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OtpCacheService {
    void storeOtp(String email, String otp);

    String getOtp(String email, String otp);

    void removeOtp(String email);

    Long getExpirationByOtpId(String email);

    Otps getOtpByUserId(String email);
}


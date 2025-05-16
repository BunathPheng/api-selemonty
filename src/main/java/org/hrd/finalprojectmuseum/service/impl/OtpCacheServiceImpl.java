package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.InvalidOptException;
import org.hrd.finalprojectmuseum.model.entity.AppUser;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.Otps;
import org.hrd.finalprojectmuseum.repository.AppUserRepository;
import org.hrd.finalprojectmuseum.repository.OtpRepository;
import org.hrd.finalprojectmuseum.service.OtpCacheService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpCacheServiceImpl implements OtpCacheService {

    private final OtpRepository otpRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public void storeOtp(String email, String otp) {
        LocalDateTime expiredDate = LocalDateTime.now().plusMinutes(2);
        AppUserRegister appUser = appUserRepository.findUserByEmail(email);
        otpRepository.saveOpt(appUser.getUserId(), expiredDate, otp);
    }

    @Override
    public String getOtp(String email, String otp) {
        AppUser appUser = appUserRepository.getUserByEmail(email)
                .orElseThrow(()->  new UsernameNotFoundException("Email is not register yet"));
        String otpCode = otpRepository.getOptCodeByUserId(appUser.getUserId());
        Otps storedOtp = otpRepository.getOptByUserId(appUser.getUserId());
        if (storedOtp == null) {
            throw new InvalidOptException("Otp is not request yet");
        }
        if (storedOtp.getExpiredDate().isBefore(LocalDateTime.now())) {
            throw new InvalidOptException("Otp is expired");
        }
        return otpCode;
    }

    @Override
    public void removeOtp(String email) {
        AppUserRegister appUser = appUserRepository.findUserByEmail(email);
        otpRepository.removeOptByUserId(appUser.getUserId());
    }

    @Override
    public LocalDateTime getExpirationByOtpId(String email) {
        AppUserRegister appUser = appUserRepository.findUserByEmail(email);
        return otpRepository.getExpirationByUserId(appUser.getUserId());
    }

    @Override
    public Otps getOtpByUserId(String email) {
        AppUserRegister appUser = appUserRepository.findUserByEmail(email);
        return otpRepository.getOptByUserId(appUser.getUserId());
    }
}

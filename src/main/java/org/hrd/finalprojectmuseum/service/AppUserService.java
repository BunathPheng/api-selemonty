package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.dto.request.auth.ChangePasswordRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.UUID;

public interface AppUserService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    AppUserRegister registerUser(String email, String password, Role role);

    AppUserRegister findUserByIdentifier (String email, String password);

    void checkEmailBeforeOpt(String email);

    void verifyEmailWithOpt(String email);

    String resetPassword(String token, String newPassword);

    void checkEmail(String email);

    String getToken(String email);

    void storeVisitor(UUID userId, String fullName);

    void storeMuseumOwner(UUID userId, String name, String logoLink, String address, BigDecimal lat, BigDecimal lng, String description);

    void updatePassword(UUID userId, ChangePasswordRequest passwordRequest);

    AppUserRegister findUserByUserId(UUID userId);

    UUID getUserId();

    AppUserRegister getAppUserRegister();
}

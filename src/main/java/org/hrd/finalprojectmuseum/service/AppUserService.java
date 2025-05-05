package org.hrd.finalprojectmuseum.service;

import jakarta.validation.Valid;
import org.hrd.finalprojectmuseum.model.dto.request.ForgotPasswordRequest;
import org.hrd.finalprojectmuseum.model.dto.request.RegisterRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;

public interface AppUserService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    AppUserRegister registerUser(@Valid RegisterRequest appUserRequest);

    AppUserRegister findUserByIdentifier (String email, String password);

    void checkEmailBeforeOpt(String email);

    void verifyEmailWithOpt(String email);

    String sendResetLink(String email) throws IOException;

    String resetPassword(String token, String newPassword);
}

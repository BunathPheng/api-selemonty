package org.hrd.finalprojectmuseum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.jwt.JwtUtils;
import org.hrd.finalprojectmuseum.model.dto.request.ForgotPasswordRequest;
import org.hrd.finalprojectmuseum.model.dto.request.LoginRequest;
import org.hrd.finalprojectmuseum.model.dto.request.RegisterRequest;
import org.hrd.finalprojectmuseum.model.dto.request.ResetPasswordRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.LoginToken;
import org.hrd.finalprojectmuseum.service.AppUserService;
import org.hrd.finalprojectmuseum.service.OtpCacheService;
import org.hrd.finalprojectmuseum.service.SendEmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auths")
public class AuthController {
    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SendEmailService sendEmailService;
    private final OtpCacheService otpCacheService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginToken>> login(@Valid @RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        AppUserRegister appUserRegister = appUserService.findUserByIdentifier(email, password);
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(appUserRegister.getEmail(), password)
        );

        if (!auth.isAuthenticated()) {
            throw new AppBadRequestException("Log in failed");
        }
        ApiResponse<LoginToken> response = ApiResponse.<LoginToken>builder()
                .success(true)
                .message("Logged in successfully")
                .status(HttpStatus.OK)
                .payload(new LoginToken(jwtUtils.generateToken(appUserRegister.getEmail(), appUserRegister.getUserId(), String.valueOf(appUserRegister.getRole()))))
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AppUserRegister>> registerUser(@RequestBody @Valid RegisterRequest registerRequest) throws IOException {

        AppUserRegister appUser = appUserService.registerUser(registerRequest);

        ApiResponse<AppUserRegister> response = ApiResponse.<AppUserRegister>builder()
                .success(true)
                .message("Registered successfully")
                .status(HttpStatus.CREATED)
                .payload(appUser)
                .build();

        String otp = sendEmailService.generateOtp();
        sendEmailService.sendOtpEmail(registerRequest.getEmail(), otp);
        otpCacheService.storeOtp(registerRequest.getEmail(), otp);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestParam String email) {
        String otp = sendEmailService.generateOtp();
        appUserService.checkEmailBeforeOpt(email);

        try {
            sendEmailService.sendOtpEmail(email, otp);
        } catch (Exception e) {
            throw new AppBadRequestException("Failed to send OTP: " + e.getMessage());
        }

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Sent OTP successfully")
                .status(HttpStatus.CREATED)
                .build();

        otpCacheService.removeOtp(email);
        otpCacheService.storeOtp(email, otp);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String email, @RequestParam String otp) {
        appUserService.checkEmailBeforeOpt(email);
        String cachedOtp = otpCacheService.getOtp(email, otp);

        if (cachedOtp == null) {
            throw new AppBadRequestException("Your OTP has been expired.");
        }

        if (!cachedOtp.equals(otp)) {
            throw new AppBadRequestException("OTP is incorrect.");
        }

        appUserService.verifyEmailWithOpt(email);
        otpCacheService.removeOtp(email);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Email has been verified successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) throws IOException {
        String email = appUserService.sendResetLink(forgotPasswordRequest.getEmail());
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Check your email to change your password")
                .payload(email)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        String email = appUserService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Your password has been reset successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

}


package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.jwt.JwtUtils;
import org.hrd.finalprojectmuseum.model.dto.request.auth.*;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.EmailDetails;
import org.hrd.finalprojectmuseum.model.entity.LoginToken;
import org.hrd.finalprojectmuseum.model.entity.Otps;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auths")
public class AuthController {
    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SendEmailService sendEmailService;
    private final OtpCacheService otpService;
    private final GoogleAuthService googleAuthService;
    private final EmailService emailService;

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

    @PostMapping("/visitor/google-login")
    public ResponseEntity<ApiResponse<LoginToken>> handleGoogleLoginAsVisitor(@RequestBody IdTokenRequest request) throws Exception {
        LoginToken userInfo = googleAuthService.verifyAndExtractUserInfo(request.getIdToken(), "VISITOR");
        ApiResponse<LoginToken> response = ApiResponse.<LoginToken>builder()
                .success(true)
                .message("Logged in successfully")
                .status(HttpStatus.OK)
                .payload(userInfo)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/museum-owner/google-login")
    public ResponseEntity<ApiResponse<LoginToken>> handleGoogleLoginAsMuseumOwner(@RequestBody IdTokenRequest request) throws Exception {
        LoginToken userInfo = googleAuthService.verifyAndExtractUserInfo(request.getIdToken(), "MUSEUM-OWNER");
        ApiResponse<LoginToken> response = ApiResponse.<LoginToken>builder()
                .success(true)
                .message("Logged in successfully")
                .status(HttpStatus.OK)
                .payload(userInfo)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/visitor/register")
    public ResponseEntity<ApiResponse<AppUserRegister>> registerVisitor(@RequestBody @Valid VisitorRegisterRequest visitorRegisterRequest) throws IOException {

        AppUserRegister appUser = appUserService.registerUser(visitorRegisterRequest.getEmail(), visitorRegisterRequest.getPassword(), Role.ROLE_VISITOR);
        appUserService.storeVisitor(appUser.getUserId(), visitorRegisterRequest.getFullName());
        ApiResponse<AppUserRegister> response = ApiResponse.<AppUserRegister>builder()
                .success(true)
                .message("Registered successfully")
                .status(HttpStatus.CREATED)
                .payload(appUser)
                .build();

        String otp = sendEmailService.generateOtp();

        emailService.sendMailAsHTML(visitorRegisterRequest.getEmail(), otp);
//        sendEmailService.sendOtpEmail(visitorRegisterRequest.getEmail(), otp);
        otpService.storeOtp(visitorRegisterRequest.getEmail(), otp);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/museum-owner/register")
    @Transactional
    public ResponseEntity<ApiResponse<AppUserRegister>> registerMuseumOwner(@RequestBody @Valid MuseumOwnerRegisterRequest museumOwnerRegisterRequest) throws IOException {

        AppUserRegister appUser = appUserService.registerUser(museumOwnerRegisterRequest.getEmail(), museumOwnerRegisterRequest.getPassword(), Role.ROLE_MUSEUM_OWNER);
        appUserService.storeMuseumOwner(appUser.getUserId(), museumOwnerRegisterRequest.getName(), museumOwnerRegisterRequest.getLogoLink(), museumOwnerRegisterRequest.getLat(), museumOwnerRegisterRequest.getLng(), museumOwnerRegisterRequest.getDescription());
        ApiResponse<AppUserRegister> response = ApiResponse.<AppUserRegister>builder()
                .success(true)
                .message("Registered successfully")
                .payload(appUser)
                .status(HttpStatus.CREATED)
                .build();
        String otp = sendEmailService.generateOtp();
        emailService.sendMailAsHTML(museumOwnerRegisterRequest.getEmail(), otp);
//        sendEmailService.sendOtpEmail(museumOwnerRegisterRequest.getEmail(), otp);
        otpService.storeOtp(museumOwnerRegisterRequest.getEmail(), otp);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Otps>> sendOtp(@RequestParam String email) {
        String otp = sendEmailService.generateOtp();
        appUserService.checkEmailBeforeOpt(email);
//        try {
////            sendEmailService.sendOtpEmail(email, otp);
//
//        } catch (Exception e) {
//            throw new AppBadRequestException("Failed to send OTP: " + e.getMessage());
//        }
        String result = emailService.sendMailAsHTML(email, otp);

        Otps opts = otpService.getOtpByUserId(email);
        ApiResponse<Otps> response = ApiResponse.<Otps>builder()
                .success(true)
                .message("Sent OTP successfully")
                .payload(opts)
                .status(HttpStatus.CREATED)
                .build();

        otpService.removeOtp(email);
        otpService.storeOtp(email, otp);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam @Email(message = "Email is wrong syntax") String email, @RequestParam String otp) {
        appUserService.checkEmailBeforeOpt(email);
        String storedOtp = otpService.getOtp(email, otp);

        if (!storedOtp.equals(otp)) {
            throw new AppBadRequestException("OTP is incorrect.");
        }

        appUserService.verifyEmailWithOpt(email);
        otpService.removeOtp(email);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Email has been verified successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Otps>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {
        String otp = sendEmailService.generateOtp();
        appUserService.checkEmail(forgotPasswordRequest.getEmail());
//        try {
//            sendEmailService.sendOtpEmail(forgotPasswordRequest.getEmail(), otp);
//        } catch (Exception e) {
//            throw new AppBadRequestException("Failed to send OTP: " + e.getMessage());
//        }
        emailService.sendMailAsHTML(forgotPasswordRequest.getEmail(), otp);
        otpService.storeOtp(forgotPasswordRequest.getEmail(), otp);
        Otps opts = otpService.getOtpByUserId(forgotPasswordRequest.getEmail());
        ApiResponse<Otps> response = ApiResponse.<Otps>builder()
                .success(true)
                .message("Check your email to change your password")
                .payload(opts)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp/forgot-password")
    public ResponseEntity<ApiResponse<String>> verifyOtpForgotPassword(@RequestParam @Email(message = "Email is wrong syntax") String email, @RequestParam String otp) {
        appUserService.checkEmail(email);
        String storedOtp = otpService.getOtp(email, otp);

        if (!storedOtp.equals(otp)) {
            throw new AppBadRequestException("OTP is incorrect.");
        }
        otpService.removeOtp(email);
        String token = appUserService.getToken(email);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Otp has been verified successfully.")
                .payload(token)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        appUserService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Your password has been reset successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/otp-expiration")
    public ResponseEntity<ApiResponse<LocalDateTime>> otpExpiration(@RequestParam @Email(message = "email is not correct syntax") String email) {
        LocalDateTime expiration = otpService.getExpirationByOtpId(email);
        ApiResponse<LocalDateTime> response = ApiResponse.<LocalDateTime>builder()
                .success(true)
                .message("Successfully get the expiration datetime")
                .status(HttpStatus.OK)
                .payload(expiration)
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/google/login")
    @Operation(summary = "For Testing only", description = "this endpoint server side flow for google sign in")
    public RedirectView googleLogin(@RequestParam(required = false, defaultValue = "VISITOR") String role) {
        String redirectUrl = "/oauth2/authorize/google";
        if (role != null && !role.isEmpty()) {
            redirectUrl += "?role=" + role;
        }
        return new RedirectView(redirectUrl);
    }


}


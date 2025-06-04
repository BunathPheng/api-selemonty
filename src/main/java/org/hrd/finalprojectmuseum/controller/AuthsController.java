package org.hrd.finalprojectmuseum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.jwt.JwtUtils;
import org.hrd.finalprojectmuseum.model.dto.request.auth.ChangePasswordRequest;
import org.hrd.finalprojectmuseum.model.dto.request.auth.*;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ForgotPasswordToken;
import org.hrd.finalprojectmuseum.model.dto.response.OtpExpiration;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.LoginToken;
import org.hrd.finalprojectmuseum.model.entity.Otps;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auths")
public class AuthsController {
    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SendEmailService sendEmailService;
    private final OtpCacheService otpService;
    private final GoogleAuthService googleAuthService;
    private final EmailService emailService;
    private final ProfileService profileService;

    @Operation(summary = "Use for login for all role")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginToken<?>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        AppUserRegister appUserRegister = appUserService.findUserByIdentifier(email, password);
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(appUserRegister.getEmail(), password)
        );

        if (!auth.isAuthenticated()) {
            throw new AppBadRequestException("Log in failed");
        }
        LoginToken<?> loginToken;
        if (appUserRegister.getRole() == Role.ROLE_ADMIN) {
            Admin admin = profileService.getAdminByUserId(appUserRegister.getUserId());
            loginToken = LoginToken.<Admin>builder()
                    .token(jwtUtils.generateToken(appUserRegister.getEmail(), appUserRegister.getUserId(), String.valueOf(appUserRegister.getRole())))
                    .user(admin)
                    .build();
        }
        else if (appUserRegister.getRole() == Role.ROLE_MUSEUM_OWNER) {
            MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(appUserRegister.getUserId());
            loginToken = LoginToken.<MuseumOwner>builder()
                    .token(jwtUtils.generateToken(appUserRegister.getEmail(), appUserRegister.getUserId(), String.valueOf(appUserRegister.getRole())))
                    .user(museumOwner)
                    .build();
        }
        else {
            Visitor visitor = profileService.getProfile(appUserRegister.getUserId());
            loginToken = LoginToken.<Visitor>builder()
                    .token(jwtUtils.generateToken(appUserRegister.getEmail(), appUserRegister.getUserId(), String.valueOf(appUserRegister.getRole())))
                    .user(visitor)
                    .build();
        }
        ApiResponse<LoginToken<?>> response = ApiResponse.<LoginToken<?>>builder()
                .success(true)
                .message("Logged in successfully")
                .status(HttpStatus.OK)
                .payload(loginToken)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login with google with IdToken as visitor", description = "This endpoint need google IdToken from frontend to verify to register or login. Can use google oauth2 playground website to get IdToken for testing.")
    @PostMapping("/google/sign-in/visitor")
    public ResponseEntity<ApiResponse<LoginToken<?>>> handleGoogleLoginAsVisitor(@RequestBody @Valid IdTokenRequest request) throws Exception {
        LoginToken<?> userInfo = googleAuthService.verifyAndExtractUserInfo(request.getIdToken(), "VISITOR");
        ApiResponse<LoginToken<?>> response = ApiResponse.<LoginToken<?>>builder()
                .success(true)
                .message("Logged in successfully")
                .status(HttpStatus.OK)
                .payload(userInfo)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login with google with IdToken as museum", description = "This endpoint need google IdToken from frontend to verify to register or login. Can use google oauth2 playground website to get IdToken for testing.")
    @PostMapping("/google/sign-in/museum-owner")
    public ResponseEntity<ApiResponse<LoginToken>> handleGoogleLoginAsMuseumOwner(@RequestBody @Valid IdTokenRequest request) throws Exception {
        LoginToken userInfo = googleAuthService.verifyAndExtractUserInfo(request.getIdToken(), "MUSEUM-OWNER");
        ApiResponse<LoginToken> response = ApiResponse.<LoginToken>builder()
                .success(true)
                .message("Logged in successfully")
                .status(HttpStatus.OK)
                .payload(userInfo)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Register as visitor role")
    @PostMapping("/register/visitor")
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

    @Operation(summary = "Register as museum owner role")
    @PostMapping("/register/museum-owner")
    @Transactional
    public ResponseEntity<ApiResponse<AppUserRegister>> registerMuseumOwner(@RequestBody @Valid MuseumOwnerRegisterRequest museumOwnerRegisterRequest) throws IOException {

        AppUserRegister appUser = appUserService.registerUser(museumOwnerRegisterRequest.getEmail(), museumOwnerRegisterRequest.getPassword(), Role.ROLE_MUSEUM_OWNER);
        appUserService.storeMuseumOwner(appUser.getUserId(), museumOwnerRegisterRequest.getName(), museumOwnerRegisterRequest.getLogoLink(), museumOwnerRegisterRequest.getAddress(), museumOwnerRegisterRequest.getLat(), museumOwnerRegisterRequest.getLng(), museumOwnerRegisterRequest.getDescription());
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

    @Operation(summary = "For send re-send otp to verify account", description = "This endpoint use for send otp to verify account if user request to resend again")
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Otps>> sendOtp(@RequestParam @Email(message = "Email form is incorrect") @NotBlank(message = "Email is required") String email) {
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

    @Operation(summary = "For Verify account using OTP", description = "After getting OTP from email, use it to verify account")
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam @Email(message = "Email form is incorrect") @NotBlank(message = "Email is required") String email, @RequestParam @NotBlank(message = "OTP is required") String otp) {
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

    @Operation(summary = "For forgot password feature", description = "After input email, OTP will send to email. Then use OTP to verify in verify-otp/forgot-password endpoint. NOTE: if you dont see OTP email send in inbox please kinda check in spam. ")
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

    @Operation(summary = "Verify OTP to confirm change password", description = "Use OTP in email to verify then it will return token. This token can be use to combine with frontend route to make sure the link use to change password can be use only in period of time and nobody can access, accepted user.")
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<ApiResponse<ForgotPasswordToken>> verifyOtpForgotPassword(@RequestParam @Email(message = "Email is wrong syntax") @NotBlank(message = "Email is required") String email, @RequestParam @NotBlank(message = "OTP is required") String otp) {
        appUserService.checkEmail(email);
        String storedOtp = otpService.getOtp(email, otp);

        if (!storedOtp.equals(otp)) {
            throw new AppBadRequestException("OTP is incorrect.");
        }
        otpService.removeOtp(email);
        String token = appUserService.getToken(email);
        ForgotPasswordToken forgotPasswordToken = ForgotPasswordToken.builder().token(token).build();
        ApiResponse<ForgotPasswordToken> response = ApiResponse.<ForgotPasswordToken>builder()
                .success(true)
                .message("Otp has been verified successfully.")
                .payload(forgotPasswordToken)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reset password after confirm all step of forgot password", description = "This endpoint use to confirm token and then change password to new password for user")
    @PostMapping("/forgot-password/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        appUserService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Your password has been reset successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "get expiration of OTP as second")
    @GetMapping("/otp-expiration")
    public ResponseEntity<ApiResponse<OtpExpiration>> otpExpiration(@RequestParam @Email(message = "Email form is incorrect") @NotBlank(message = "Email is required") String email) {
        Long expiration = otpService.getExpirationByOtpId(email);
        OtpExpiration otpExpiration = OtpExpiration.builder().expiration(expiration).build();
        ApiResponse<OtpExpiration> response = ApiResponse.<OtpExpiration>builder()
                .success(true)
                .message("Successfully get the expiration datetime")
                .status(HttpStatus.OK)
                .payload(otpExpiration)
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

    @Operation(summary = "Use old password to change password")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@RequestBody @Valid ChangePasswordRequest passwordRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) auth.getCredentials());
        appUserService.updatePassword(userId, passwordRequest);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Password has been updated successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}


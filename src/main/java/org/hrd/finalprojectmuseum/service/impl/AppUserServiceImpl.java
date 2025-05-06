package org.hrd.finalprojectmuseum.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.ThrowFieldException;
import org.hrd.finalprojectmuseum.jwt.JwtUtils;
import org.hrd.finalprojectmuseum.model.dto.request.auth.VisitorRegisterRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUser;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.repository.AppUserRepository;
import org.hrd.finalprojectmuseum.service.AppUserService;
import org.hrd.finalprojectmuseum.service.SendEmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ModelMapper mapper = new ModelMapper();
    private final SendEmailService sendEmailService;
    @Value("${app.url.resetpassword}")
    String urlResetPassword;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = appUserRepository.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public AppUserRegister registerUser(String email, String password, Role role) {
        AppUser findUser = appUserRepository.findUserByEmail(email);
        if (findUser != null) {
            throw new ThrowFieldException("email", "Email has already taken");
        }

        String encodedPass = passwordEncoder.encode(password);
        AppUser appUser = appUserRepository.registerUser(email, encodedPass, role);
        AppUserRegister appUserResponse = mapper.map(appUserRepository.getUserById(appUser.getUserId()), AppUserRegister.class);
        return appUserResponse;
    }

    @Override
    public AppUserRegister findUserByIdentifier(String email, String password) {
        AppUser appUser = appUserRepository.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (appUser == null) throw new AppBadRequestException("Invalid username, email, or password. Please check your credentials and try again.");

        boolean isCorrect = passwordEncoder.matches(password, appUser.getPassword());
        if (!isCorrect) throw new AppBadRequestException("Invalid username, email, or password. Please check your credentials and try again.");

        if (!appUser.getIsVerified()) throw new AppBadRequestException("User has not verified yet.");

        AppUserRegister appUserResponse = mapper.map(appUser, AppUserRegister.class);


        return appUserResponse;
    }

    @Override
    public void checkEmailBeforeOpt(String email) {
        AppUser appUser = appUserRepository.findUserByEmail(email);
        if (appUser == null) {
            throw new AppBadRequestException("Your email has not registered yet.");
        }

        if (appUser.getIsVerified()) {
            throw new AppBadRequestException("Your email has already verified.");
        }
    }



    @Override
    public void verifyEmailWithOpt(String email) {
        appUserRepository.verifyEmailWithOpt(email);
    }


    @Override
    public String resetPassword(String token, String newPassword) {
        String email;
        try {
            email = jwtUtils.extractEmail(token);
        } catch (Exception e) {
            throw new AppBadRequestException("Invalid or expired token");
        }
        AppUser user = appUserRepository.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserDetails userDetails = new User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
        if (!jwtUtils.isTokenValid(token, userDetails)) {
            throw new AppBadRequestException("Invalid or expired token");
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        appUserRepository.updatePassword(user);
        return email;
    }

    @Override
    public void checkEmail(String email) {
        AppUser appUser = appUserRepository.findUserByEmail(email);
        if (appUser == null) {
            throw new AppBadRequestException("Email not found.");
        }
    }

    @Override
    public String getToken(String email) {
        appUserRepository.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        return jwtUtils.generateResetToken(email);
    }

    @Override
    public void storeVisitor(UUID userId, String fullName) {
        appUserRepository.storeVistitor(userId, fullName);
    }

    @Override
    public void storeMuseumOwner(UUID userId, String name, String logoLink, BigDecimal lat, Double lng, String description) {
        appUserRepository.storeMeseumOwner(userId, name, logoLink, lat, lng, description);
    }
}


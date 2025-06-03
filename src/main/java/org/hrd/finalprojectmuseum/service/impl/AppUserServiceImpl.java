package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.exception.ThrowFieldException;
import org.hrd.finalprojectmuseum.jwt.JwtUtils;
import org.hrd.finalprojectmuseum.model.dto.request.auth.ChangePasswordRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUser;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.repository.AppUserRepository;
import org.hrd.finalprojectmuseum.service.AppUserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = appUserRepository.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public AppUserRegister registerUser(String email, String password, Role role) {
        AppUserRegister findUser = appUserRepository.findUserByEmail(email);
        if (findUser != null && findUser.getIsVerified()) {
            throw new ThrowFieldException("email", "Email has already taken");
        }
        //remove user if email is already registered before but not verify yet
        else if(findUser != null) {
            appUserRepository.deleteUser(findUser.getUserId());
        }
        String encodedPass = passwordEncoder.encode(password);
        AppUserRegister appUser = appUserRepository.registerUser(email, encodedPass, role, false);
        return mapper.map(appUserRepository.getUserById(appUser.getUserId()), AppUserRegister.class);
    }

    @Override
    public AppUserRegister findUserByIdentifier(String email, String password) {
        AppUser appUser = appUserRepository.getUserByEmail(email)
                .orElseThrow(() -> new AppNotFoundException("User not found"));
        if (appUser == null) throw new AppBadRequestException("Invalid email, or password. Please check your credentials and try again.");

        boolean isCorrect = passwordEncoder.matches(password, appUser.getPassword());
        if (!isCorrect) throw new AppBadRequestException("Invalid email, or password. Please check your credentials and try again.");

        if (!appUser.getIsVerified()) throw new AppBadRequestException("User has not verified yet.");

        return mapper.map(appUser, AppUserRegister.class);
    }

    @Override
    public void checkEmailBeforeOpt(String email) {
        AppUserRegister appUser = appUserRepository.findUserByEmail(email);
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
        AppUserRegister appUser = appUserRepository.findUserByEmail(email);
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
        appUserRepository.storeVisitor(userId, fullName, null);
    }

    @Override
    public void storeMuseumOwner(UUID userId, String name, String logoLink, BigDecimal lat, BigDecimal lng, String description) {
        appUserRepository.storeMeseumOwner(userId, name, logoLink, lat, lng, description);
    }

    @Override
    public void updatePassword(UUID userId, ChangePasswordRequest passwordRequest) {
        AppUserRegister appUserRegister = appUserRepository.getUserById(userId);
        if (appUserRegister == null) {
            throw new AppNotFoundException("Invalid user. Please login first.");
        }
        boolean isCorrect = passwordEncoder.matches(passwordRequest.getOldPassword(), appUserRegister.getPassword());
        if (!isCorrect) throw new AppBadRequestException("Invalid old password. Please check your old password and try again.");
        appUserRepository.updatePasswordByUserId(userId, passwordEncoder.encode(passwordRequest.getNewPassword()));
    }

    @Override
    public AppUserRegister findUserByUserId(UUID userId) {
        AppUserRegister appUserRegister = appUserRepository.getUserById(userId);
        if (appUserRegister == null) {
            throw new AppBadRequestException("User with id " + userId + " does not exist.");
        }
        return appUserRegister;
    }

    @Override
    public UUID getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString((String) auth.getCredentials());
    }

    @Override
    public AppUserRegister getAppUserRegister() {
        return findUserByUserId(getUserId());
    }
}


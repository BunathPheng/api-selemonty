package org.hrd.finalprojectmuseum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hrd.finalprojectmuseum.model.dto.request.auth.VisitorRegisterRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
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

    void storeMuseumOwner(UUID userId, @NotBlank(message = "Name is required") @Size(min = 3, message = "Name must be at least 3 characters") @Size(max = 50, message = "Name cannot be greater than 50 characters") String name, String logoLink, @NotNull(message = "Name is required") @Digits(integer = 4, fraction = 6, message = "Must be a number with up to 4 integer digits and 6 fractional digits") BigDecimal lat, @NotNull(message = "Name is required") @Digits(integer = 4, fraction = 6, message = "Must be a number with up to 4 integer digits and 6 fractional digits") Double lng, String description);
}

package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.dto.request.admin.ChangePasswordRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.repository.AdminRepository;
import org.hrd.finalprojectmuseum.repository.AppUserRepository;
import org.hrd.finalprojectmuseum.service.AdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.hrd.finalprojectmuseum.utils.RequestUtils.getOrDefault;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;

    @Override
    public Admin getAdminByUserId(UUID userId) {
        Admin admin = adminRepository.findAdminByUserId(userId);
        if (admin == null) {
            throw new AppNotFoundException("Invalid user. Please login first.");
        }
        return admin;
    }

    @Override
    public Admin updateAdminByUserId(UUID userId, AdminRequest adminRequest) {
        Admin admin = getAdminByUserId(userId);
        AdminRequest update = new AdminRequest();
        update.setName(getOrDefault(adminRequest.getName(), admin.getName()));
        update.setProfileImageLink(getOrDefault(adminRequest.getProfileImageLink(), admin.getProfileImageLink()));
        return adminRepository.modifyAdminByAdminId(admin.getAdminId(), update);
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
}

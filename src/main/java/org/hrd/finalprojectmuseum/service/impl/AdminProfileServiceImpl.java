package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;
import org.hrd.finalprojectmuseum.repository.AdminRepository;
import org.hrd.finalprojectmuseum.repository.MuseumOwnerRepository;
import org.hrd.finalprojectmuseum.service.AdminProfileService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.hrd.finalprojectmuseum.utils.RequestUtils.getOrDefault;

@Service
@RequiredArgsConstructor
public class AdminProfileServiceImpl implements AdminProfileService {

    private final AdminRepository adminRepository;
    private final MuseumOwnerRepository museumOwnerRepository;

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
}

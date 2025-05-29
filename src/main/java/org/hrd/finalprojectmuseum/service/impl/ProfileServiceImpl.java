package org.hrd.finalprojectmuseum.service.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.PaymentAccountRequest;
import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumOwnerRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumCategory;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.repository.*;
import org.hrd.finalprojectmuseum.service.ProfileService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hrd.finalprojectmuseum.utils.RequestUtils.getOrDefault;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final MuseumRepository museumRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public MuseumOwner getMuseumOwnerByUserId(UUID userId) {
        MuseumOwner museumOwner = profileRepository.findMuseumOwnerByUserId(userId);
        if (museumOwner == null) {
            throw new AppNotFoundException("Museum Owner Not Found");
        }
        return museumOwner;
    }

    @Override
    public MuseumOwner updateMuseumOwnerByUserId(UUID userId, MuseumOwnerRequest request) {
        MuseumOwner existing = getMuseumOwnerByUserId(userId);

        MuseumOwnerRequest updatedRequest = new MuseumOwnerRequest();

        updatedRequest.setName(getOrDefault(request.getName(), existing.getName()));
        if (existing.getMuseumCategory() == null){
            updatedRequest.setMuseumCategoryId(request.getMuseumCategoryId());
        }else{
            updatedRequest.setMuseumCategoryId(Optional.ofNullable(request.getMuseumCategoryId()).orElse(existing.getMuseumCategory().getMuseumCategoryId()));
        }
        updatedRequest.setContactNumber(getOrDefault(request.getContactNumber(), existing.getContactNumber()));
        updatedRequest.setLat(Optional.ofNullable(request.getLat()).orElse(existing.getLat()));
        updatedRequest.setLng(Optional.ofNullable(request.getLng()).orElse(existing.getLng()));
        updatedRequest.setLogoLink(getOrDefault(request.getLogoLink(), existing.getLogoLink()));
        updatedRequest.setBannerLink(getOrDefault(request.getBannerLink(), existing.getBannerLink()));
        updatedRequest.setLandscapeLink(request.getLandscapeLink()!=null?request.getLandscapeLink():existing.getLandscapeLink());
        updatedRequest.setDescription(getOrDefault(request.getDescription(), existing.getDescription()));
        profileRepository.modifyMuseumOwnerById(existing.getMuseumId(), updatedRequest, LocalDateTime.now());
        return getMuseumOwnerByUserId(userId);
    }

    @Override
    public void deleteMuseumOwnerByUserId(UUID userId) {
        MuseumOwner museumOwner = getMuseumOwnerByUserId(userId);
        profileRepository.removeMuseumOwnerByMuseumId(museumOwner.getMuseumId());
    }

    @Override
    public JSONObject addLanscapeByUserId(UUID userId, JSONObject landscapeRequest) {
        MuseumOwner museumOwner = getMuseumOwnerByUserId(userId);
        JSONObject existLandscape = museumOwner.getLandscapeLink();
        if (existLandscape == null) {
            existLandscape = new JSONObject();
        }
        existLandscape.putAll(landscapeRequest);

        return profileRepository.modifyLandscapeByMuseumId(museumOwner.getMuseumId(), existLandscape);
    }

    @Override
    public void deleteLandscapeByUserId(UUID userId, String landscapeKey) {
        MuseumOwner museumOwner = getMuseumOwnerByUserId(userId);
        JSONObject existLandscape = museumOwner.getLandscapeLink();
        if (existLandscape == null) {
            existLandscape = new JSONObject();
        }
        if (existLandscape.containsKey(landscapeKey)) {
            existLandscape.remove(landscapeKey);
        }else {
            throw new AppNotFoundException("Landscape Key Not Found");
        }
        profileRepository.modifyLandscapeByMuseumId(museumOwner.getMuseumId(), existLandscape);
    }

    @Override
    public List<MuseumCategory> getMuseumCategories() {
        List<MuseumCategory> museumCategories = museumRepository.getMuseumCategories();
        if (museumCategories == null) {
            throw new AppNotFoundException("Museum Categories Not Found");
        }
        return museumCategories;
    }

    @Override
    public MuseumOwner updateMuseumOwnerPaymentByUserId(UUID userId, PaymentAccountRequest paymentAccountRequest) {
        getMuseumOwnerByUserId(userId);
        return profileRepository.updateMuseumPaymentByUserId(userId, paymentAccountRequest);
    }
    
    //For admin

    @Override
    public Admin getAdminByUserId(UUID userId) {
        Admin admin = profileRepository.findAdminByUserId(userId);
        if (admin == null) {
            throw new AppNotFoundException("Invalid user. Please login first.");
        }
        return admin;
    }

    @Override
    public Admin updateAdminByUserId(UUID userId, AdminRequest adminRequest) {
        Admin admin = getAdminByUserId(userId);
        return profileRepository.modifyAdminByAdminId(admin.getAdminId(), adminRequest);
    }
    
    // visitor

    @Override
    public Visitor getProfile(UUID userId) {
        Visitor visitor = profileRepository.findVisitor(userId);
        if (visitor == null) {
            throw new AppNotFoundException("Visitor not found");
        }
        return visitor;
    }

    @Override
    public Visitor updateVisitor(UUID userId, VisitorRequest request) {
        Visitor visitor = profileRepository.findVisitor(userId);
        if (visitor == null) {
            throw new AppNotFoundException("Visitor not found");
        }
        
        return profileRepository.modifyVisitorByVisitorId(userId, request, LocalDateTime.now());
    }


    @Override
    public void deleteVisitor(UUID userId) {
        AppUserRegister appUser = appUserRepository.getUserById(userId);
        if (appUser == null) {
            throw new AppNotFoundException("UserId is wrong");
        }
        appUserRepository.deleteUser(userId);
    }
}

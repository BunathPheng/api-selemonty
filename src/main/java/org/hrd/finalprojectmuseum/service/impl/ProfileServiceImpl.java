package org.hrd.finalprojectmuseum.service.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.PaymentAccountRequest;
import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumOwnerRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.Schedule;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumCategory;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReviewStatistics;
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
    private final ReviewRepository reviewRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    public MuseumOwner getMuseumOwnerByUserId(UUID userId) {
        MuseumOwner museumOwner = profileRepository.findMuseumOwnerByUserId(userId);
        VisitorReviewStatistics reviewStatistics = reviewRepository.retriveVisitorReviewStatistics(museumOwner.getMuseumId());
        List<Schedule> schedules = scheduleRepository.findScheduleOfMuseum(museumOwner.getMuseumId());
        Schedule todaySchedule = scheduleRepository.findScheduleOfMuseumByDay(museumOwner.getMuseumId(), LocalDateTime.now().getDayOfWeek().toString());
        museumOwner.setReview(reviewStatistics);
        museumOwner.setSchedule(schedules);
        museumOwner.setTodaySchedule(todaySchedule);
        if (museumOwner == null) {
            throw new AppNotFoundException("Museum Owner Not Found");
        }
        System.out.println("museum: "+museumOwner);
        return museumOwner;
    }

    @Override
    public MuseumOwner updateMuseumOwnerByUserId(UUID userId, MuseumOwnerRequest request) {
        if (!museumRepository.isMuseumCategoriesExist(request.getMuseumCategoryId())){
            throw new AppNotFoundException("Museum Category Not Found");
        }
        MuseumOwner existing = getMuseumOwnerByUserId(userId);
        profileRepository.modifyMuseumOwnerById(existing.getMuseumId(), request, LocalDateTime.now());
        MuseumOwner updatedMuseum = getMuseumOwnerByUserId(userId);
        VisitorReviewStatistics reviewStatistics = reviewRepository.retriveVisitorReviewStatistics(updatedMuseum.getMuseumId());
        List<Schedule> schedules = scheduleRepository.findScheduleOfMuseum(updatedMuseum.getMuseumId());
        Schedule todaySchedule = scheduleRepository.findScheduleOfMuseumByDay(updatedMuseum.getMuseumId(), LocalDateTime.now().getDayOfWeek().toString());
        updatedMuseum.setReview(reviewStatistics);
        updatedMuseum.setSchedule(schedules);
        updatedMuseum.setTodaySchedule(todaySchedule);
        return updatedMuseum;
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
        MuseumOwner updatedMuseum = profileRepository.updateMuseumPaymentByUserId(userId, paymentAccountRequest, LocalDateTime.now());
        VisitorReviewStatistics reviewStatistics = reviewRepository.retriveVisitorReviewStatistics(updatedMuseum.getMuseumId());
        List<Schedule> schedules = scheduleRepository.findScheduleOfMuseum(updatedMuseum.getMuseumId());
        Schedule todaySchedule = scheduleRepository.findScheduleOfMuseumByDay(updatedMuseum.getMuseumId(), LocalDateTime.now().getDayOfWeek().toString());
        updatedMuseum.setReview(reviewStatistics);
        updatedMuseum.setSchedule(schedules);
        updatedMuseum.setTodaySchedule(todaySchedule);
        return updatedMuseum;
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

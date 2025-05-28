package org.hrd.finalprojectmuseum.service.impl;


import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumWithDistanceResponse;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.Schedule;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReview;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReviewStatistics;
import org.hrd.finalprojectmuseum.repository.MuseumManagementForAdminRepository;
import org.hrd.finalprojectmuseum.repository.MuseumOwnerRepository;
import org.hrd.finalprojectmuseum.service.MuseumManagementForAdminService;
import org.hrd.finalprojectmuseum.service.ScheduleService;
import org.hrd.finalprojectmuseum.service.visitor.VisitorReviewService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MuseumManagementForAdminServiceImpl implements MuseumManagementForAdminService {

    private final MuseumManagementForAdminRepository museumManagementForAdminRepository;
    private final MuseumOwnerRepository museumOwnerRepository;
    private final ScheduleService scheduleService;
    private final VisitorReviewService visitorReviewService;

    @Override
    public ListResponse<MuseumShortInfo> getAllRequestMuseum(UUID museumCategoryId, Integer page, Integer size) {
        List<MuseumShortInfo> museums;
        if (museumCategoryId == null){
            museums = museumManagementForAdminRepository.getAllRequestMuseums(page, size);
        }else {
            museums = museumManagementForAdminRepository.getAllRequestMuseumsByCategoryId(museumCategoryId, page, size);
        }
        Integer totalItems = museumManagementForAdminRepository.countAllRequestMuseums();

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);

        ListResponse<MuseumShortInfo> listMuseumResponse = ListResponse.<MuseumShortInfo>builder()
                .items(museums)
                .pagination(pagination)
                .build();
        return listMuseumResponse;
    }


    @Override
    public void approveMuseum(UUID museumId) {
        MuseumOwner museum = museumOwnerRepository.findMuseumOwnerByMuseumId(museumId);
        if(museum == null) {
            throw new AppNotFoundException("Museum not found. Please check museum id and try again.");
        }
        if (museum.getIsApproved()) {
            throw new AppBadRequestException("Museum is already approved.");
        }
        museumManagementForAdminRepository.udpateIsApprovedStatus(museumId);

    }

    @Override
    public ListResponse<MuseumShortInfo> getAllMuseum(String search, UUID museumCategoryId, Integer page, Integer size) {
        List<MuseumShortInfo> museums;
        Integer totalItems;
        search = search == null ? "" : search;
        if (museumCategoryId == null){
            museums = museumManagementForAdminRepository.getAllMuseums(search, page, size);
            totalItems = museumManagementForAdminRepository.countAllMuseums(search);
        }else {
            museums = museumManagementForAdminRepository.getAllMuseumsByCategoryId(search, museumCategoryId, page, size);
            totalItems = museumManagementForAdminRepository.countAllMuseumsByCategory(search, museumCategoryId);
        }

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);

        ListResponse<MuseumShortInfo> listMuseumResponse = ListResponse.<MuseumShortInfo>builder()
                .items(museums)
                .pagination(pagination)
                .build();
        listMuseumResponse.setItems(museums);
        listMuseumResponse.setPagination(pagination);
        return listMuseumResponse;
    }

    @Override
    public ListResponse<MuseumShortInfo> getAllApprovedMuseum(UUID museumCategoryId, Integer page, Integer size) {
        List<MuseumShortInfo> museums;
        if (museumCategoryId == null){
            museums = museumManagementForAdminRepository.getAllApprovedMuseums(page, size);
        }else {
            museums = museumManagementForAdminRepository.getAllApprovedMuseumsByCategoryId(museumCategoryId, page, size);
        }

        Integer totalItems = museumManagementForAdminRepository.countAllApprovedMuseums();

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);

        ListResponse<MuseumShortInfo> listMuseumResponse = ListResponse.<MuseumShortInfo>builder()
                .items(museums)
                .pagination(pagination)
                .build();
        listMuseumResponse.setItems(museums);
        listMuseumResponse.setPagination(pagination);
        return listMuseumResponse;
    }

    @Override
    public List<MuseumWithDistanceResponse> getAllMuseumByLocation(BigDecimal lat, BigDecimal lng, Integer distance) {
        List<MuseumWithDistanceResponse> nearbyMuseums = museumManagementForAdminRepository
                .findNearbyMuseumsOptimized(lat, lng, distance);
        String day = LocalDateTime.now().getDayOfWeek().toString();
        System.out.println("today is "+day);
        for (MuseumWithDistanceResponse museum : nearbyMuseums) {
            Schedule schedule = scheduleService.getScheduleByDay(museum.getMuseumId(), day);
            VisitorReviewStatistics visitorReviewStatistics = visitorReviewService.getVisitorReviewStatistics(museum.getMuseumId());
            if (schedule != null) {
                museum.setOpenTime(schedule.getOpeningTime());
                museum.setCloseTime(schedule.getClosingTime());
            }
            if (visitorReviewStatistics != null) {
                museum.setAverageRating(visitorReviewStatistics.getAverageRating());
                museum.setTotalReviews(visitorReviewStatistics.getTotalReviews());
            }
        }

        return nearbyMuseums;


    }
}

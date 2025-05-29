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
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReviewStatistics;

import org.hrd.finalprojectmuseum.repository.MuseumRepository;
import org.hrd.finalprojectmuseum.service.MuseumService;
import org.hrd.finalprojectmuseum.service.ScheduleService;
import org.hrd.finalprojectmuseum.service.ReviewService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MuseumServiceImpl implements MuseumService {
    private final MuseumRepository museumRepository;
    private final ScheduleService scheduleService;
    private final ReviewService reviewService;

    @Override
    public ListResponse<MuseumShortInfo> getAllRequestMuseum(UUID museumCategoryId, Integer page, Integer size) {
        List<MuseumShortInfo> museums;
        if (museumCategoryId == null){
            museums = museumRepository.getAllRequestMuseums(page, size);
        }else {
            museums = museumRepository.getAllRequestMuseumsByCategoryId(museumCategoryId, page, size);
        }
        Integer totalItems = museumRepository.countAllRequestMuseums();

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
        MuseumOwner museum = museumRepository.findMuseumOwnerByMuseumId(museumId);
        if(museum == null) {
            throw new AppNotFoundException("Museum not found. Please check museum id and try again.");
        }
        if (museum.getIsApproved()) {
            throw new AppBadRequestException("Museum is already approved.");
        }
        museumRepository.udpateIsApprovedStatus(museumId);

    }

    @Override
    public ListResponse<MuseumShortInfo> getAllMuseum(String search, UUID museumCategoryId, Integer page, Integer size) {
        List<MuseumShortInfo> museums;
        Integer totalItems;
        search = search == null ? "" : search;
        if (museumCategoryId == null){
            museums = museumRepository.getAllMuseums(search, page, size);
            totalItems = museumRepository.countAllMuseums(search);
        }else {
            museums = museumRepository.getAllMuseumsByCategoryId(search, museumCategoryId, page, size);
            totalItems = museumRepository.countAllMuseumsByCategory(search, museumCategoryId);
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
            museums = museumRepository.getAllApprovedMuseums(page, size);
        }else {
            museums = museumRepository.getAllApprovedMuseumsByCategoryId(museumCategoryId, page, size);
        }

        Integer totalItems = museumRepository.countAllApprovedMuseums();

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
        List<MuseumWithDistanceResponse> nearbyMuseums = museumRepository
                .findNearbyMuseumsOptimized(lat, lng, distance);
        String day = LocalDateTime.now().getDayOfWeek().toString();
        System.out.println("today is "+day);
        for (MuseumWithDistanceResponse museum : nearbyMuseums) {
            Schedule schedule = scheduleService.getScheduleByDay(museum.getMuseumId(), day);
            VisitorReviewStatistics visitorReviewStatistics = reviewService.getVisitorReviewStatistics(museum.getMuseumId());
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

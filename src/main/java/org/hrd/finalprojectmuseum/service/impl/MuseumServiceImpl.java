package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumWithDistanceResponse;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.Schedule;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReviewStatistics;

import org.hrd.finalprojectmuseum.model.enums.MuseumStatus;
import org.hrd.finalprojectmuseum.repository.MuseumRepository;
import org.hrd.finalprojectmuseum.repository.ReviewRepository;
import org.hrd.finalprojectmuseum.repository.ScheduleRepository;
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
    private final ReviewRepository reviewRepository;
    private final ScheduleRepository scheduleRepository;


    @Override
    public MuseumOwner setFullData(MuseumOwner museumOwner) {
        VisitorReviewStatistics reviewStatistics = reviewRepository.retriveVisitorReviewStatistics(museumOwner.getMuseumId());
        List<Schedule> schedules = scheduleRepository.findScheduleOfMuseum(museumOwner.getMuseumId());
        Schedule todaySchedule = scheduleRepository.findScheduleOfMuseumByDay(museumOwner.getMuseumId(), LocalDateTime.now().getDayOfWeek().toString());
        if (reviewStatistics != null) {
            museumOwner.setReview(reviewStatistics);
        }
        if (schedules != null) {
            museumOwner.setSchedule(schedules);
            museumOwner.setTodaySchedule(todaySchedule);
        }

        return museumOwner;
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
        setFullData(museum);
        museumRepository.udpateIsApprovedStatus(museumId);

    }

    @Override
    public ListResponse<MuseumOwner> getAllMuseum(String search, UUID museumCategoryId, Integer page, Integer size, MuseumStatus museumStatus) {
        List<MuseumOwner> museums;
        Integer totalItems;
        search = search == null ? "" : search;
        if (museumCategoryId == null){
            if (museumStatus == MuseumStatus.ALL){
                museums = museumRepository.getAllMuseums(search, page, size);
                totalItems = museumRepository.countAllMuseums(search);
            }else if (museumStatus == MuseumStatus.APPROVED){
                museums = museumRepository.getAllMuseumsWithStatus(search, page, size, true);
                totalItems = museumRepository.countAllMuseumsWithStatus(search, true);
            }else {
                museums = museumRepository.getAllMuseumsWithStatus(search, page, size, false);
                totalItems = museumRepository.countAllMuseumsWithStatus(search, false);
            }
        }else {
            if (museumStatus == MuseumStatus.ALL){
                museums = museumRepository.getAllMuseumsByCategoryId(search, museumCategoryId, page, size);
                totalItems = museumRepository.countAllMuseumsByCategory(search, museumCategoryId);
            }else if (museumStatus == MuseumStatus.APPROVED){
                museums = museumRepository.getAllMuseumsByCategoryIdAndStatus(search, museumCategoryId, page, size, true);
                totalItems = museumRepository.countAllMuseumsByCategoryAndStatus(search, museumCategoryId, true);
            }else {
                museums = museumRepository.getAllMuseumsByCategoryIdAndStatus(search, museumCategoryId, page, size, false);
                totalItems = museumRepository.countAllMuseumsByCategoryAndStatus(search, museumCategoryId, false);
            }
        }
        for (MuseumOwner museum : museums) {
            setFullData(museum);
        }

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);

        ListResponse<MuseumOwner> listMuseumResponse = ListResponse.<MuseumOwner>builder()
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

    @Override
    public MuseumOwner getAllMuseumByMuseumId(UUID museumId) {
        MuseumOwner museum = museumRepository.findMuseumOwnerByMuseumId(museumId);
        if (museum == null) {
            throw new AppNotFoundException("Museum not found");
        }
        return museum;
    }
}

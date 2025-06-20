package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumWithDistanceResponse;
import org.hrd.finalprojectmuseum.model.entity.*;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReviewStatistics;

import org.hrd.finalprojectmuseum.model.enums.MuseumStatus;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.model.enums.SortMuseum;
import org.hrd.finalprojectmuseum.repository.*;
import org.hrd.finalprojectmuseum.service.*;
import org.hrd.finalprojectmuseum.utils.Calculation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final AppUserService appUserService;
    private final ProfileRepository profileRepository;
    private final Calculation calculation = new Calculation();
    private final OneSignalService oneSignalService;
    private final TicketInfoService ticketInfoService;
    private final TicketInfoRepository ticketInfoRepository;

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

    @Transactional
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

        oneSignalService.sendToUser(
                appUserService.getAdminUserId(),
                "Museum approval",
                "Congratulation! Your museum have been approved"
        ).subscribe();
    }

    @Override
    public ListResponse<MuseumOwner> getAllMuseum(String search, UUID museumCategoryId, Integer page, Integer size, SortMuseum museumSort, MuseumStatus museumStatus) {
        List<MuseumOwner> museums;
        Integer totalItems;
        search = search == null ? "" : search;
        if (museumSort == SortMuseum.latest) {
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
        } else {
            if (museumCategoryId == null){
                if (museumStatus == MuseumStatus.ALL){
                    museums = museumRepository.getAllPopularMuseumsPopular(search, page, size);
                    totalItems = museumRepository.countAllPopularMuseums(search);
                }else if (museumStatus == MuseumStatus.APPROVED){
                    museums = museumRepository.getAllPopularMuseumsWithStatus(search, page, size, true);
                    totalItems = museumRepository.countAllMuseumsWithStatus(search, true);
                }else {
                    museums = museumRepository.getAllPopularMuseumsWithStatus(search, page, size, false);
                    totalItems = museumRepository.countPopularAllMuseumsWithStatus(search, false);
                }
            }else {
                if (museumStatus == MuseumStatus.ALL){
                    museums = museumRepository.getAllPopularMuseumsByCategoryId(search, museumCategoryId, page, size);
                    totalItems = museumRepository.countAllPopularMuseumsByCategory(search, museumCategoryId);
                }else if (museumStatus == MuseumStatus.APPROVED){
                    museums = museumRepository.getAllPopularMuseumsByCategoryIdAndStatus(search, museumCategoryId, page, size, true);
                    totalItems = museumRepository.countAllPopularMuseumsByCategoryAndStatus(search, museumCategoryId, true);
                }else {
                    museums = museumRepository.getAllPopularMuseumsByCategoryIdAndStatus(search, museumCategoryId, page, size, false);
                    totalItems = museumRepository.countAllPopularMuseumsByCategoryAndStatus(search, museumCategoryId, false);
                }
            }
        }

        for (MuseumOwner museum : museums) {
            setFullData(museum);
            TicketInfo ticketInfo= ticketInfoRepository.findTicketInfoByMuseumId(museum.getMuseumId());
            if (ticketInfo != null){
                museum.setLocalPrice(ticketInfo.getLocalPrice());
                museum.setForeignPrice(ticketInfo.getForeignPrice());
            }


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
    public ListResponse<MuseumOwner> getAllMuseumForVisitor(UUID visitorId, String search, UUID museumCategoryId, Integer page, Integer size, SortMuseum museumSort, MuseumStatus museumStatus) {
        List<MuseumOwner> museums;
        Integer totalItems;
        search = search == null ? "" : search;
        if (museumSort == SortMuseum.latest) {
            if (museumCategoryId == null){
                if (museumStatus == MuseumStatus.ALL){
                    museums = museumRepository.getAllMuseumsForVisitor(visitorId, search, page, size);
                    totalItems = museumRepository.countAllMuseumsForVisitor(visitorId, search);
                }else if (museumStatus == MuseumStatus.APPROVED){
                    museums = museumRepository.getAllMuseumsWithStatusForVisitor(visitorId, search, page, size, true);
                    totalItems = museumRepository.countAllMuseumsWithStatusForVisitor(visitorId, search, true);
                }else {
                    museums = museumRepository.getAllMuseumsWithStatusForVisitor(visitorId, search, page, size, false);
                    totalItems = museumRepository.countAllMuseumsWithStatusForVisitor(visitorId, search, false);
                }
            }else {
                if (museumStatus == MuseumStatus.ALL){
                    museums = museumRepository.getAllMuseumsByCategoryIdForVisitor(visitorId, search, museumCategoryId, page, size);
                    totalItems = museumRepository.countAllMuseumsByCategoryForVisitor(visitorId, search, museumCategoryId);
                }else if (museumStatus == MuseumStatus.APPROVED){
                    museums = museumRepository.getAllMuseumsByCategoryIdAndStatusForVisitor(visitorId, search, museumCategoryId, page, size, true);
                    totalItems = museumRepository.countAllMuseumsByCategoryAndStatusForVisitor(visitorId, search, museumCategoryId, true);
                }else {
                    museums = museumRepository.getAllMuseumsByCategoryIdAndStatusForVisitor(visitorId, search, museumCategoryId, page, size, false);
                    totalItems = museumRepository.countAllMuseumsByCategoryAndStatusForVisitor(visitorId, search, museumCategoryId, false);
                }
            }
        } else {
            if (museumCategoryId == null){
                if (museumStatus == MuseumStatus.ALL){
                    museums = museumRepository.getAllPopularMuseumsPopularForVisitor(visitorId, search, page, size);
                    totalItems = museumRepository.countAllPopularMuseumsForVisitor(visitorId, search);
                }else if (museumStatus == MuseumStatus.APPROVED){
                    museums = museumRepository.getAllPopularMuseumsWithStatusForVisitor(visitorId, search, page, size, true);
                    totalItems = museumRepository.countAllMuseumsWithStatusForVisitor(visitorId, search, true);
                }else {
                    museums = museumRepository.getAllPopularMuseumsWithStatusForVisitor(visitorId, search, page, size, false);
                    totalItems = museumRepository.countPopularAllMuseumsWithStatusForVisitor(visitorId, search, false);
                }
            }else {
                if (museumStatus == MuseumStatus.ALL){
                    museums = museumRepository.getAllPopularMuseumsByCategoryIdForVisitor(visitorId, search, museumCategoryId, page, size);
                    totalItems = museumRepository.countAllPopularMuseumsByCategoryForVisitor(visitorId, search, museumCategoryId);
                }else if (museumStatus == MuseumStatus.APPROVED){
                    museums = museumRepository.getAllPopularMuseumsByCategoryIdAndStatusForVisitor(visitorId, search, museumCategoryId, page, size, true);
                    totalItems = museumRepository.countAllPopularMuseumsByCategoryAndStatusForVisitor(visitorId, search, museumCategoryId, true);
                }else {
                    museums = museumRepository.getAllPopularMuseumsByCategoryIdAndStatusForVisitor(visitorId, search, museumCategoryId, page, size, false);
                    totalItems = museumRepository.countAllPopularMuseumsByCategoryAndStatusForVisitor(visitorId, search, museumCategoryId, false);
                }
            }
        }

        for (MuseumOwner museum : museums) {
            setFullData(museum);
            TicketInfo ticketInfo= ticketInfoRepository.findTicketInfoByMuseumId(museum.getMuseumId());
            if(ticketInfo != null){
                museum.setLocalPrice(ticketInfo.getLocalPrice());
                museum.setForeignPrice(ticketInfo.getForeignPrice());
            }

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
    public MuseumStat getMuseumStat() {
        LocalDate today = LocalDate.now();
        LocalDate currentMonthStart = today.withDayOfMonth(1);

        LocalDate lastMonthStart = currentMonthStart.minusMonths(1);
        LocalDate lastMonthEnd = today.withDayOfMonth(1).minusDays(1);

        Integer currentTotalMuseum = museumRepository.retrieveTotalMuseumByDateRange(today);
        Integer lastMonthTotalMuseum = museumRepository.retrieveTotalMuseumByDateRange(lastMonthEnd);

        Integer currentNewMuseum = museumRepository.retrieveMuseumByDateRange(currentMonthStart, today);
        Integer lastMonthNewMuseum = museumRepository.retrieveMuseumByDateRange(lastMonthStart, lastMonthEnd);
        return MuseumStat.builder()
                .totalMuseum(calculation.addStatItem(currentTotalMuseum, lastMonthTotalMuseum))
                .newMuseum(calculation.addStatItem(currentNewMuseum, lastMonthNewMuseum))
                .build();
    }

    @Override
    public List<MuseumWithDistanceResponse> getAllMuseumByLocation(BigDecimal lat, BigDecimal lng, Integer distance) {
        AppUserRegister appUser = appUserService.getAppUserRegister();
        List<MuseumWithDistanceResponse> nearbyMuseums;
        if (appUser != null && appUser.getRole() == Role.ROLE_VISITOR){
            Visitor visitor = profileRepository.findVisitor(appUser.getUserId());
            nearbyMuseums = museumRepository.findNearbyMuseumsOptimizedForVisitor(visitor.getVisitorId(), lat, lng, distance);
        }else{
            nearbyMuseums = museumRepository.findNearbyMuseumsOptimized(lat, lng, distance);
        }


        String day = LocalDateTime.now().getDayOfWeek().toString();
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
        setFullData(museum);
        return museum;
    }

    @Override
    public ListResponse<MuseumOwner> getAllMuseumOrderbyPopular(Integer page, Integer size) {
        List<MuseumOwner> museumOwners = museumRepository.findAllMuseumOrderbyPopular(page, size);
        Pagination pagination = new Pagination();
        Integer countMuseum = museumRepository.countAllMuseumOrderbyPopular();
        return ListResponse.<MuseumOwner>builder()
                .items(museumOwners)
                .pagination(pagination.calculatePagination(countMuseum, page, size))
                .build();
    }
}

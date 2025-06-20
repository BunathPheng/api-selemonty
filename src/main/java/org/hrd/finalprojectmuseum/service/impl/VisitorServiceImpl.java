package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.StatItem;
import org.hrd.finalprojectmuseum.model.entity.*;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwnerVisitorStat;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.repository.BookingRepository;
import org.hrd.finalprojectmuseum.repository.ProfileRepository;
import org.hrd.finalprojectmuseum.repository.VisitorRepository;
import org.hrd.finalprojectmuseum.service.AppUserService;
import org.hrd.finalprojectmuseum.service.VisitorService;
import org.hrd.finalprojectmuseum.utils.Calculation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;
    private final ProfileRepository profileRepository;
    private final BookingRepository bookingRepository;
    private final AppUserService appUserService;
    private final Calculation calculation = new Calculation();


    @Override
    public ListResponse<Visitor> getVisitorByUserId(UUID userId, String search, Integer page, Integer size) {
        MuseumOwner museumOwner = profileRepository.findMuseumOwnerByUserId(userId);
        if (museumOwner == null){
            throw new AppNotFoundException("User with id " + userId + " not found");
        }
        List<Visitor> visitors = visitorRepository.findVisitorByMuseumId(museumOwner.getMuseumId(), search, page, size);
        Integer allItem = visitorRepository.countAllVisitorByMuseumId(museumOwner.getMuseumId(), search);
        Pagination pagination = new Pagination();
        return ListResponse.<Visitor>builder()
                .items(visitors)
                .pagination(pagination.calculatePagination(allItem, page, size))
                .build();
    }

    @Override
    public ListResponse<Visitor> getAllVisitor(String search, Integer page, Integer size) {
        search = search == null ? "" : search;
        List<Visitor> visitors = visitorRepository.findAllVisitor(search, page, size);
        Integer allItem = visitorRepository.countAllVisitor(search);
        Pagination pagination = new Pagination();
        return ListResponse.<Visitor>builder()
                .items(visitors)
                .pagination(pagination.calculatePagination(allItem, page, size))
                .build();
    }

    @Override
    public Visitor getVisitorById(UUID visitorId) {
        Visitor visitor = visitorRepository.findVisitorById(visitorId);
        if (visitor == null){
            throw new AppNotFoundException("Visitor with id " + visitorId + " not found");
        }
        return visitor;
    }

    @Override
    public ListResponse<VisitorBooking> getAllVisitorBooking(String search, Integer page, Integer size) {
        search = search == null ? "" : search;
        List<VisitorBooking> visitorBookings = visitorRepository.findAllVisitorBooking(search, page, size);
        Integer totalVisitor = visitorRepository.countVisitorBooking(search);
        Pagination pagination = new Pagination();
        return ListResponse.<VisitorBooking>builder()
                .items(visitorBookings)
                .pagination(pagination.calculatePagination(totalVisitor, page, size))
                .build();
    }

    @Override
    public ListResponse<VisitorBookingDetail> getBookingByVisitorId(UUID visitorId, String search, Integer page, Integer size) {
        Visitor visitor = visitorRepository.findVisitorById(visitorId);
        if (visitor == null){
            throw new AppNotFoundException("Visitor with id " + visitorId + " not found");
        }
        search = search == null ? "" : search;
        List<VisitorBookingDetail> bookingDetail = visitorRepository.findVisitorBookingByVisitorId(visitorId, search, page, size);
        Pagination pagination = new Pagination();
        Integer totalItem = visitorRepository.countBookingByVisitorId(visitorId, search);
        return ListResponse.<VisitorBookingDetail>builder()
                .items(bookingDetail)
                .pagination(pagination.calculatePagination(totalItem, page, size))
                .build();
    }

    @Override
    public VisitorBookingTotal getVisitorBookingTotalByVisitorId(UUID visitorId) {
        Visitor visitor = visitorRepository.findVisitorById(visitorId);
        if (visitor == null){
            throw new AppNotFoundException("Visitor with id " + visitorId + " not found");
        }
        VisitorBookingTotal visitorBookingTotal = visitorRepository.retrieveBookingTotalByVisitorId(visitorId);
        VisitorBookingTotal thisMonthBooking = visitorRepository.retrieveThisMonthBookingTotalByVisitorId(visitorId);
        VisitorBookingTotal lastMonthBooking = visitorRepository.retrieveLastMonthBookingTotalByVisitorId(visitorId);
        double percentTageTicket = 0;
        double percentTageBooking = 0;
        if (thisMonthBooking.getTotalBooking() > 0){
            percentTageTicket = 100;
            percentTageBooking = 100;
        }
        if (lastMonthBooking.getTotalBooking() > 0 || lastMonthBooking.getTotalTicket() > 0){
            percentTageTicket = (double) (thisMonthBooking.getTotalTicket() - lastMonthBooking.getTotalTicket()) / lastMonthBooking.getTotalTicket() * 100;
            percentTageBooking = (double) (thisMonthBooking.getTotalBooking() - lastMonthBooking.getTotalBooking()) / lastMonthBooking.getTotalBooking() * 100;
        }
        visitorBookingTotal.setCompareLastMonthBooking(percentTageBooking);
        visitorBookingTotal.setCompareLastMonthTicket(percentTageTicket);
        return visitorBookingTotal;
    }

    @Override
    public VisitorStat getVisitorStatByVisitorId() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayEnd = now.toLocalDate().atTime(23, 59, 59);

        LocalDate currentMonthStart = now.toLocalDate().withDayOfMonth(1);
        LocalDateTime currentMonthStartTime = currentMonthStart.atStartOfDay();

        LocalDate lastMonthStart = currentMonthStart.minusMonths(1);
        LocalDateTime lastMonthStartTime = lastMonthStart.atStartOfDay();
        LocalDate lastMonthEnd = currentMonthStart.minusDays(1);
        LocalDateTime lastMonthEndTime = lastMonthEnd.atTime(23, 59, 59);

        Integer currentNewVisitors = visitorRepository.countNewVisitorsByDateRange(currentMonthStartTime, todayEnd);
        Integer currentTotalVisitors = visitorRepository.countTotalVisitors(todayEnd);
        Integer currentTotalBookings = bookingRepository.countBookingsByDateRange(currentMonthStartTime, todayEnd);

        Integer lastMonthNewVisitors = visitorRepository.countNewVisitorsByDateRange(lastMonthStartTime, lastMonthEndTime);
        Integer lastMonthTotalVisitors = visitorRepository.countTotalVisitors(currentMonthStartTime);
        Integer lastMonthTotalBookings = bookingRepository.countBookingsByDateRange(lastMonthStartTime, lastMonthEndTime);

        return VisitorStat.builder()
                .totalVisitors(calculation.addStatItem(currentTotalVisitors, lastMonthTotalVisitors))
                .newVisitors(calculation.addStatItem(currentNewVisitors, lastMonthNewVisitors))
                .newBooking(calculation.addStatItem(currentTotalBookings, lastMonthTotalBookings))
                .build();
    }

    @Override
    public List<Visitor> getTopVisitorByMuseumId(UUID museumId) {
        return visitorRepository.findTopVisitorByMuseumId(museumId);
    }

    @Override
    public MuseumOwnerVisitorStat getVisitorStatistic() {
        UUID userID = appUserService.getUserId();
        MuseumOwner museumOwner = profileRepository.findMuseumOwnerByUserId(userID);

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime todayEnd = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);

        // Current month boundaries
        LocalDate currentMonthStart = today.withDayOfMonth(1);
        LocalDateTime currentMonthStartTime = currentMonthStart.atStartOfDay();

        // Last month boundaries
        LocalDate lastMonthStart = currentMonthStart.minusMonths(1);
        LocalDateTime lastMonthStartTime = lastMonthStart.atStartOfDay();
        LocalDate lastMonthEnd = currentMonthStart.minusDays(1);
        LocalDateTime lastMonthEndTime = lastMonthEnd.atTime(23, 59, 59);

        // Get total visitors (cumulative up to today)
        Integer totalVisitorsThisMonth = visitorRepository.countVisitorByMuseumIdAndDateRange(
                museumOwner.getMuseumId(), currentMonthStartTime, todayEnd);
        Integer totalVisitorsLastMonth = visitorRepository.countVisitorByMuseumIdAndDateRange(
                museumOwner.getMuseumId(), lastMonthStartTime, lastMonthEndTime);

        // Get new visitors (first-time visitors in each period)
        Integer newVisitorsThisMonth = visitorRepository.countNewVisitorByMuseumIdAndDateRange(
                museumOwner.getMuseumId(), currentMonthStartTime, todayEnd);
        Integer newVisitorsLastMonth = visitorRepository.countNewVisitorByMuseumIdAndDateRange(
                museumOwner.getMuseumId(), lastMonthStartTime, lastMonthEndTime);

        return MuseumOwnerVisitorStat.builder()
                .totalVisitors(createStatItem(totalVisitorsThisMonth, totalVisitorsLastMonth))
                .newVisitors(createStatItem(newVisitorsThisMonth, newVisitorsLastMonth))
                .build();
    }

    private StatItem createStatItem(Integer currentValue, Integer previousValue) {
        Double percentageChange = calculatePercentageChange(currentValue, previousValue);

        return StatItem.builder()
                .value(currentValue != null ? currentValue : 0)
                .percentageChange(percentageChange)
                .build();
    }

    private Double calculatePercentageChange(Integer currentValue, Integer previousValue) {
        if (previousValue == null || previousValue == 0) {
            return currentValue != null && currentValue > 0 ? 100.0 : 0.0;
        }

        if (currentValue == null) {
            return -100.0;
        }

        double change = ((currentValue.doubleValue() - previousValue.doubleValue()) / previousValue.doubleValue()) * 100;
        return Math.round(change * 100.0) / 100.0; // Round to 2 decimal places
    }
}

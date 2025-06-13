package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.StatItem;
import org.hrd.finalprojectmuseum.model.entity.*;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.repository.BookingRepository;
import org.hrd.finalprojectmuseum.repository.ProfileRepository;
import org.hrd.finalprojectmuseum.repository.VisitorRepository;
import org.hrd.finalprojectmuseum.service.VisitorService;
import org.hrd.finalprojectmuseum.utils.Calculation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;
    private final ProfileRepository profileRepository;
    private final BookingRepository bookingRepository;
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
        LocalDate today = LocalDate.now();
        LocalDate currentMonthStart = today.withDayOfMonth(1);

        LocalDate lastMonthStart = currentMonthStart.minusMonths(1);
        LocalDate lastMonthEnd = today.withDayOfMonth(1).minusDays(1);

        Integer currentNewVisitors = visitorRepository.countNewVisitorsByDateRange(currentMonthStart, today);
        Integer currentTotalVisitors = visitorRepository.countTotalVisitors(today);
        Integer currentTotalBookings = bookingRepository.countBookingsByDateRange(currentMonthStart, today);

        Integer lastMonthNewVisitors = visitorRepository.countNewVisitorsByDateRange(lastMonthStart, lastMonthEnd);
        Integer lastMonthTotalVisitors = visitorRepository.countTotalVisitors(currentMonthStart);
        Integer lastMonthTotalBookings = bookingRepository.countBookingsByDateRange(lastMonthStart, lastMonthEnd);

        return VisitorStat.builder()
                .totalVisitors(calculation.addStatItem(currentTotalVisitors, lastMonthTotalVisitors))
                .newVisitors(calculation.addStatItem(currentNewVisitors, lastMonthNewVisitors))
                .newBooking(calculation.addStatItem(currentTotalBookings, lastMonthTotalBookings))
                .build();
    }
}

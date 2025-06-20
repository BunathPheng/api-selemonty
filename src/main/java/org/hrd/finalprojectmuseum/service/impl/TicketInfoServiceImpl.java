package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.TicketInfoRequest;
import org.hrd.finalprojectmuseum.model.dto.response.StatItem;
import org.hrd.finalprojectmuseum.model.entity.TicketInfo;
import org.hrd.finalprojectmuseum.model.entity.TicketStat;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.repository.*;
import org.hrd.finalprojectmuseum.service.AppUserService;
import org.hrd.finalprojectmuseum.service.TicketInfoService;
import org.hrd.finalprojectmuseum.service.VisitorService;
import org.hrd.finalprojectmuseum.utils.Calculation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketInfoServiceImpl implements TicketInfoService {

    private final TicketInfoRepository ticketInfoRepository;
    private final Calculation calculation = new Calculation();
    private final AppUserService appUserService;
    private final ProfileRepository profileRepository;

    @Override
    public TicketInfo getTicketInfoByMuseumId(UUID museumId) {
        TicketInfo ticketInfo = ticketInfoRepository.findTicketInfoByMuseumId(museumId);
        if (ticketInfo == null) {
            throw new AppNotFoundException("Ticket not found");
        }
        return ticketInfo;
    }

    @Override
    public void addTicketInfo(UUID museumId) {
        ticketInfoRepository.insertTicketInfo(museumId);
    }

    @Override
    public TicketInfo updateTicketInfo(UUID museumId, TicketInfoRequest ticketInfoRequest) {
        return ticketInfoRepository.modifyTicketInfo(museumId, ticketInfoRequest);
    }

    @Override
    public TicketStat getTicketStat() {
        UUID userId = appUserService.getUserId();
        UUID museumId = profileRepository.getMuseumIdByUserId(userId);
        LocalDate today = LocalDate.now();
        LocalDate currentMonthStart = today.withDayOfMonth(1);

        LocalDate lastMonthStart = currentMonthStart.minusMonths(1);
        LocalDate lastMonthEnd = today.withDayOfMonth(1).minusDays(1);

        Integer totalSold = ticketInfoRepository.countTotalSoldByMuseumId(museumId, today);
        Integer totalSoldLastMonth = ticketInfoRepository.countTotalSoldByMuseumIdAndDateRange(museumId, lastMonthStart, lastMonthEnd);
        Integer totalSoldNewMonth = ticketInfoRepository.countTotalSoldByMuseumIdAndDateRange(museumId, lastMonthStart, lastMonthEnd);

        Integer totalTicketSold = ticketInfoRepository.countTotalTicketSoldByMuseumId(museumId, today);
        Integer totalTicketSoldLastMonth = ticketInfoRepository.countTotalTicketSoldByMuseumIdAndRangeDate(museumId, lastMonthStart, lastMonthEnd);
        Integer totalTicketSoldNewMonth = ticketInfoRepository.countTotalTicketSoldByMuseumIdAndRangeDate(museumId, currentMonthStart, today);

        Double percentageSold = calculation.calculatePercentageChange(totalSoldNewMonth, totalSoldLastMonth);
        Double percentageTicketSold = calculation.calculatePercentageChange(totalTicketSoldNewMonth, totalTicketSoldLastMonth);

        return TicketStat.builder()
                .totalSales(StatItem.builder().value(totalSold).percentageChange(percentageSold).build())
                .totalTicket(StatItem.builder().value(totalTicketSold).percentageChange(percentageTicketSold).build())
                .build();
    }
}

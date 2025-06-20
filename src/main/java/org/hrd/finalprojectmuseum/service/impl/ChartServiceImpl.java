package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.FollowerTrendChartResponse;
import org.hrd.finalprojectmuseum.model.dto.response.VisitorTrendChartResponse;
import org.hrd.finalprojectmuseum.model.entity.BookingChart;
import org.hrd.finalprojectmuseum.model.entity.MuseumChart;
import org.hrd.finalprojectmuseum.model.entity.VisitorChart;
import org.hrd.finalprojectmuseum.model.enums.YearFilter;
import org.hrd.finalprojectmuseum.repository.ChartRepository;
import org.hrd.finalprojectmuseum.service.AppUserService;
import org.hrd.finalprojectmuseum.service.ChartService;
import org.hrd.finalprojectmuseum.service.ProfileService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private static final String[] MONTH_NAMES = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    private static final String[] SHORT_MONTHS = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private final ChartRepository chartRepository;
    private final AppUserService appUserService;
    private final ProfileService profileService;

    @Override
    public List<FollowerTrendChartResponse> getFollowerChart(YearFilter yearFilter) {
        UUID museumId = profileService.getMuseumIdByUserId();
        int targetYear = yearFilter == YearFilter.THIS_YEAR ?
                LocalDate.now().getYear() :
                LocalDate.now().getYear() - 1;

        List<Map<String, Object>> rawData = chartRepository.getMonthlyFollowerStats(museumId, targetYear);

        Map<Integer, Map<String, Object>> dataMap = rawData.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row.get("month")).intValue(), // Get "month" column
                        row -> row
                ));

        List<FollowerTrendChartResponse> monthlyData = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            FollowerTrendChartResponse data = new FollowerTrendChartResponse();
            data.setMonth(SHORT_MONTHS[month - 1]);
            data.setFullMonth(MONTH_NAMES[month - 1]);

            if (dataMap.containsKey(month)) {
                Map<String, Object> row = dataMap.get(month);
                data.setFollowers(((Number) row.get("followers")).intValue());
            } else {
                data.setFollowers(0);
            }

            monthlyData.add(data);
        }

        return monthlyData;
    }

    @Override
    public List<VisitorTrendChartResponse> getVisitorChartByMuseum(YearFilter yearFilter) {
        UUID museumId = profileService.getMuseumIdByUserId();
        int targetYear = yearFilter == YearFilter.THIS_YEAR ?
                LocalDate.now().getYear() : // 2025
                LocalDate.now().getYear() - 1; // 2024

        List<Map<String, Object>> rawData = chartRepository.getMonthlyVisitorStatsByMuseum(museumId, targetYear);

        Map<Integer, Map<String, Object>> dataMap = rawData.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row.get("month")).intValue(), // Now safe with integer month
                        row -> row
                ));

        List<VisitorTrendChartResponse> monthlyData = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            VisitorTrendChartResponse data = new VisitorTrendChartResponse();
            data.setMonth(SHORT_MONTHS[month - 1]);
            data.setFullMonth(MONTH_NAMES[month - 1]);

            if (dataMap.containsKey(month)) {
                Map<String, Object> row = dataMap.get(month);
                data.setVisitors(((Number) row.get("visitors")).intValue());
            } else {
                data.setVisitors(0);
            }

            monthlyData.add(data);
        }

        return monthlyData;
    }

    @Override
    public List<BookingChart> getBookingChart(YearFilter yearFilter) {
        UUID museumId = profileService.getMuseumIdByUserId();
        int targetYear = yearFilter == YearFilter.THIS_YEAR ?
                LocalDate.now().getYear() :
                LocalDate.now().getYear() - 1;

        List<Map<String, Object>> rawData = chartRepository.getMonthlyBookingStats(museumId, targetYear);

        Map<Integer, Map<String, Integer>> monthlyData = new HashMap<>();

        for (int month = 1; month <= 12; month++) {
            Map<String, Integer> bookingTypes = new HashMap<>();
            bookingTypes.put("TOUR", 0);
            bookingTypes.put("INDIVIDUAL", 0);
            monthlyData.put(month, bookingTypes);
        }

        for (Map<String, Object> row : rawData) {
            int month = ((Number) row.get("month")).intValue();
            String bookingType = (String) row.get("booking_type");
            int count = ((Number) row.get("total_booking")).intValue();

            monthlyData.get(month).put(bookingType, count);
        }

        List<BookingChart> result = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            BookingChart chart = new BookingChart();
            chart.setMonth(SHORT_MONTHS[month - 1]);
            chart.setFullMonth(MONTH_NAMES[month - 1]);

            Map<String, Integer> bookingTypes = monthlyData.get(month);
            chart.setTours(bookingTypes.get("TOUR"));
            chart.setIndividuals(bookingTypes.get("INDIVIDUAL"));

            result.add(chart);
        }

        return result;
    }

    @Override
    public List<VisitorChart> getVisitorChart(YearFilter yearFilter) {
        int targetYear = yearFilter == YearFilter.THIS_YEAR ?
                LocalDate.now().getYear() :
                LocalDate.now().getYear() - 1;

        List<Map<String, Object>> rawData = chartRepository.getMonthlyVisitorStats(targetYear);

        Map<Integer, Map<String, Object>> dataMap = rawData.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row.get("month")).intValue(),
                        row -> row
                ));

        List<VisitorChart> monthlyData = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            VisitorChart data = new VisitorChart();
            data.setMonth(SHORT_MONTHS[month - 1]);
            data.setFullMonth(MONTH_NAMES[month - 1]);

            if (dataMap.containsKey(month)) {
                Map<String, Object> row = dataMap.get(month);
                data.setVisitors(((Number) row.get("visitors")).intValue());
            } else {
                data.setVisitors(0);
            }

            monthlyData.add(data);
        }

        return monthlyData;
    }

    @Override
    public MuseumChart getMuseumChart(YearFilter yearFilter) {
        int targetYear = yearFilter == YearFilter.THIS_YEAR ?
                LocalDate.now().getYear() :
                LocalDate.now().getYear() - 1;
        return chartRepository.getMuseumStat(targetYear);
    }
}

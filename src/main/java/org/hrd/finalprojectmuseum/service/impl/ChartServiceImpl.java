package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.FollowerTrendChartResponse;
import org.hrd.finalprojectmuseum.model.enums.YearFilter;
import org.hrd.finalprojectmuseum.repository.ChartRepository;
import org.hrd.finalprojectmuseum.service.ChartService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

//    private static final String[] MONTH_NAMES = {
//            "January", "February", "March", "April", "May", "June",
//            "July", "August", "September", "October", "November", "December"
//    };
//
//    private static final String[] SHORT_MONTHS = {
//            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
//            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
//    };
//    private final ChartRepository chartRepository;
//
    @Override
    public List<FollowerTrendChartResponse> getFollowerChart(YearFilter yearFilter) {
//        int targetYear = yearFilter == YearFilter.THIS_YEAR ?
//                LocalDate.now().getYear() :
//                LocalDate.now().getYear() - 1;
//
//        List<Object[]> rawData = chartRepository.getMonthlyFollowerStats(targetYear);
//
//        // Create a map for easy lookup
//        Map<Integer, Object[]> dataMap = rawData.stream()
//                .collect(Collectors.toMap(
//                        row -> ((Number) row[0]).intValue(),
//                        row -> row
//                ));
//
//        // Build monthly data for all 12 months
//        List<FollowerTrendChartResponse> followerChart = new ArrayList<>();
//
//        for (int month = 1; month <= 12; month++) {
//            FollowerTrendChartResponse data = FollowerTrendChartResponse.builder()
//                    .month(SHORT_MONTHS[month - 1])
//                    .followers(followerChart)
//                    .build();
//            data.setMonth(SHORT_MONTHS[month - 1]);
//            data.setFullMonth(MONTH_NAMES[month - 1]);
//
//            if (dataMap.containsKey(month)) {
//                Object[] row = dataMap.get(month);
//                data.setTotal(((Number) row[1]).intValue());
//                data.setConfirmed(((Number) row[2]).intValue());
//            } else {
//                // No data for this month
//                data.setTotal(0);
//                data.setConfirmed(0);
//            }
//
//            monthlyData.add(data);
//        }
//
//        // Calculate summary
//        BookingSummary summary = new BookingSummary();
//        summary.setTotalBookings(monthlyData.stream().mapToInt(MonthlyBookingData::getTotal).sum());
//        summary.setConfirmedBookings(monthlyData.stream().mapToInt(MonthlyBookingData::getConfirmed).sum());
//        summary.setConfirmationRate(
//                summary.getTotalBookings() > 0 ?
//                        (summary.getConfirmedBookings() * 100.0) / summary.getTotalBookings() : 0.0
//        );
//
//        // Build response
//        BookingAnalyticsResponse response = new BookingAnalyticsResponse();
//        response.setData(monthlyData);
//        response.setPeriod(targetYear + "");
//        response.setSummary(summary);

//        return response;
        return null;
    }
}

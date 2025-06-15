package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.dto.response.FollowerTrendChartResponse;
import org.hrd.finalprojectmuseum.model.entity.BookingChart;
import org.hrd.finalprojectmuseum.model.entity.VisitorChart;
import org.hrd.finalprojectmuseum.model.enums.YearFilter;

import java.util.List;

public interface ChartService {

    List<FollowerTrendChartResponse> getFollowerChart(YearFilter yearFilter);

    List<BookingChart> getBookingChart(YearFilter yearFilter);

    List<VisitorChart> getVisitorChart(YearFilter yearFilter);
}

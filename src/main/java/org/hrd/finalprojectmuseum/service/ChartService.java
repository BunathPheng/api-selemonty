package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.dto.response.FollowerTrendChartResponse;
import org.hrd.finalprojectmuseum.model.enums.YearFilter;

import java.util.List;

public interface ChartService {

    List<FollowerTrendChartResponse> getFollowerChart(YearFilter yearFilter);
}

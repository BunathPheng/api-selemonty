package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChartRepository {

    @Select(""" 
        SELECT EXTRACT(MONTH FROM created_at) as month,
               COUNT(*) as follwers FROM favorites 
        WHERE visitor_id = #{visitorId}::UUID
        AND EXTRACT(YEAR FROM b.created_date) = :year\s
            GROUP BY EXTRACT(MONTH FROM b.created_date)
            ORDER BY EXTRACT(MONTH FROM b.created_date)
    """)
    List<Object[]> getMonthlyFollowerStats(int targetYear);
}

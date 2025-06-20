package org.hrd.finalprojectmuseum.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.hrd.finalprojectmuseum.model.entity.MuseumChart;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper
public interface ChartRepository {

    @Select(""" 
        SELECT EXTRACT(MONTH FROM created_at) as month,
               COUNT(*) as followers FROM favorites
        WHERE museum_id = #{museumId}::UUID
        AND is_favorite = true
        AND EXTRACT(YEAR FROM created_at) = #{targetYear}
                    GROUP BY EXTRACT(MONTH FROM created_at)
                    ORDER BY EXTRACT(MONTH FROM created_at)
    """)
    @Results(id = "followerChartMapper", value = {
            @Result(property = "month", column = "month"),
            @Result(property = "followers", column = "followers")
    })
    List<Map<String, Object>> getMonthlyFollowerStats(UUID museumId, int targetYear);

    @Select("""
        WITH monthly_visitors AS (
            SELECT 
                EXTRACT(MONTH FROM booking_date) AS month,
                COUNT(DISTINCT visitor_id) AS visitor_count
            FROM bookings
            WHERE museum_id = #{museumId}::UUID
                AND EXTRACT(YEAR FROM booking_date) = #{targetYear}
                AND ticket_status = 'VALID'
            GROUP BY EXTRACT(MONTH FROM booking_date)
        ),
        all_months AS (
            SELECT generate_series(1, 12) AS month
        )
        SELECT 
            all_months.month AS month, -- Raw integer month for mapping
            COALESCE(monthly_visitors.visitor_count, 0)::INTEGER AS visitors
        FROM all_months
        LEFT JOIN monthly_visitors ON all_months.month = monthly_visitors.month
        ORDER BY all_months.month
    """)
    @Results(id = "visitorChartMapper", value = {
            @Result(property = "month", column = "month"),
            @Result(property = "visitors", column = "visitors")
    })
    List<Map<String, Object>> getMonthlyVisitorStatsByMuseum(UUID museumId, int targetYear);

    @Select(""" 
        SELECT EXTRACT(MONTH FROM b.created_at) as month,
               b.booking_type,
               COUNT(*) as total_booking 
        FROM bookings b
        LEFT JOIN tours t ON b.booking_id = t.booking_id
        WHERE b.museum_id = #{museumId}::UUID
        AND (t.tour_id IS NULL OR (t.tour_id IS NOT NULL AND t.status = 'PAID'))
        AND EXTRACT(YEAR FROM b.created_at) = #{targetYear}
        GROUP BY EXTRACT(MONTH FROM b.created_at), b.booking_type
        ORDER BY EXTRACT(MONTH FROM b.created_at), b.booking_type
    """)
    List<Map<String, Object>> getMonthlyBookingStats(UUID museumId, int targetYear);

    @Select(""" 
        SELECT EXTRACT(MONTH FROM v.created_at) as month,
               COUNT(*) as visitors
        FROM visitors v INNER JOIN user_info u ON u.user_id = v.user_id
        WHERE u.is_verified = true
        AND EXTRACT(YEAR FROM v.created_at) = #{targetYear}
                    GROUP BY EXTRACT(MONTH FROM v.created_at)
                    ORDER BY EXTRACT(MONTH FROM v.created_at)
    """)
    List<Map<String, Object>> getMonthlyVisitorStats(int targetYear);

    @Select("""
        SELECT
            COUNT(museum_id) as totalMuseum,
            COUNT(CASE WHEN is_approved = true THEN 1 END) as approvedMuseum,
            COUNT(CASE WHEN is_approved = false THEN 1 END) as pendingMuseum
        FROM museum_owners m INNER JOIN user_info u ON m.user_id = u.user_id
        WHERE EXTRACT(YEAR FROM m.created_at) = #{targetYear}
        AND u.is_verified = true
    """)
    @Results(value = {
            @Result(property = "totalMuseum", column = "totalMuseum"),
            @Result(property = "approvedMuseum", column = "approvedMuseum"),
            @Result(property = "pendingMuseum", column = "pendingMuseum")
    })
    MuseumChart getMuseumStat(int targetYear);
}

package org.hrd.finalprojectmuseum.model.entity.museum_owner;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.Schedule;
import org.hrd.finalprojectmuseum.model.entity.TicketInfo;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReviewStatistics;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class MuseumOwner {
    private UUID museumId;
    private AppUserRegister appUserRegister;
    private MuseumCategory museumCategory;
    private VisitorReviewStatistics review;
    private Boolean isFavorite;
    private String name;
    private String address;
    private String contactNumber;
    private BigDecimal lat;
    private BigDecimal lng;
    private String logoLink;
    private String bannerLink;
    private JSONObject landscapeLink;
    private String description;
    private BigDecimal localPrice;
    private BigDecimal foreignPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalZone;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalArtifact;
    private Boolean isApproved;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Schedule> schedule;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Schedule todaySchedule;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MuseumArtifact museumArtifact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

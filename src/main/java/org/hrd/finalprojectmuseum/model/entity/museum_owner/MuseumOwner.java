package org.hrd.finalprojectmuseum.model.entity.museum_owner;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MuseumOwner {
    private UUID museumId;
    private AppUserRegister appUserRegister;
    private MuseumCategory museumCategory;
    private String name;
    private String contactNumber;
    private BigDecimal lat;
    private BigDecimal lng;
    private String logoLink;
    private String bannerLink;
    private JSONObject landscapeLink;
    private String description;
    private Boolean isApproved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

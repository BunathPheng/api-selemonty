package org.hrd.finalprojectmuseum.model.entity.museum_owner;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.AppUser;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.repository.AppUserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Data
public class MuseumOwner {
    private UUID museumId;
    private AppUserRegister appUserRegister;
    private String name;
    private String contactNumber;
    private BigDecimal lat;
    private BigDecimal lng;
    private String logoLink;
    private String bannerLink;
    private Map<String, Objects> landscape;
    private String description;
    private Boolean isApproved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

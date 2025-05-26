package org.hrd.finalprojectmuseum.model.entity.museum_owner;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.Pagination;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MuseumShortInfo {
    private UUID museumId;
    private MuseumCategory museumCategory;
    private String name;
    private String contactNumber;
    private BigDecimal lat;
    private BigDecimal lng;
    private String logoLink;
    private String description;
    private Boolean isApproved;
}

package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Guide {
    private UUID guideId;
    private UUID museumId;
    private String guideName;
    private String contactNumber;
    private String staticQrLink;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

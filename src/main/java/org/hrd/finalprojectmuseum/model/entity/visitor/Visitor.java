package org.hrd.finalprojectmuseum.model.entity.visitor;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Visitor {
    private UUID visitorId;
    private AppUserRegister appUserRegister;
    private String fullName;
    private String contactNumber;
    private String gender;
    private LocalDate dob;
    private Long bookingCount;
    private String profileImageLink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

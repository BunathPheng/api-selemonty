package org.hrd.finalprojectmuseum.model.entity.admin;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.AppUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Admin {
    private UUID adminId;
    private AppUser appUser;
    private String name;
    private String profileImageLink;
    private LocalDateTime createdAt;
}

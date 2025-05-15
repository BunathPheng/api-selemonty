package org.hrd.finalprojectmuseum.model.entity.admin;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.AppUser;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Admin {
    private UUID adminId;
    private AppUserRegister appUserRegister;
    private String name;
    private String profileImageLink;
    private LocalDateTime createdAt;
}

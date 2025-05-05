package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AppUserRegister {
    private UUID userId;
    private String email;
    private String password;
    private Role role;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

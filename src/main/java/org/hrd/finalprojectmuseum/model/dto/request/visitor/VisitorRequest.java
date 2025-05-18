package org.hrd.finalprojectmuseum.model.dto.request.visitor;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.AppUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VisitorRequest {
    @Pattern(
            regexp = "^$|[A-Za-z ]{2,255}",
            message = "Full name must be 2–255 letters"
    )
    private String fullName;

    @Pattern(
            regexp = "^$|\\d{7,25}",
            message = "Contact number must be 7–25 digits"
    )
    private String contactNumber;

    @Pattern(
            regexp = "^$|Male|Female",
            message = "Gender must be 'Male', 'Female'"
    )
    private String gender;

    @Past(message = "Birth date must be in the past")
    private LocalDate dob;

    private String profileImageLink;

}

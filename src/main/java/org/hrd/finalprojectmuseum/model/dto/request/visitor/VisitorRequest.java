package org.hrd.finalprojectmuseum.model.dto.request.visitor;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.hrd.finalprojectmuseum.utils.valide_age.MinAge;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class VisitorRequest {
    @Pattern(
            regexp = "^$|^[A-Za-z]+(\\s[A-Za-z]+)*$",
            message = "Full name must contain only letters and single spaces between words"
    )
    @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
    private String fullName;

    @Pattern(
            regexp = "^$|^[0-9]{7,25}$",
            message = "Contact number must be 7–25 digits only"
    )
    private String contactNumber;

    @Pattern(
            regexp = "^$|^(Male|Female)$",
            message = "Gender must be 'Male' or 'Female'"
    )
    private String gender;

    @Past(message = "Birth date must be in the past")
    @NotNull(message = "Date of birth is required")
    @MinAge(value = 16, message = "You must be at least 16 years old")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @URL(message = "Profile image link must be a valid URL")
    private String profileImageLink;

}

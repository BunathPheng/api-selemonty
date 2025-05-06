package org.hrd.finalprojectmuseum.model.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.enums.Role;

@Data
public class RegisterRequest {
//    @NotBlank(message = "Username is required")
//    @Size(min = 3, message = "Username must be at least 3 characters")
//    @Size(max = 50, message = "Username cannot be greater than 50 characters")
//    private String name;

    @Schema(example = "example@gmail.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    @Size(max = 255, message = "Email cannot be greater than 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Size(max = 32, message = "Password cannot be greater than 32 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    private Role role;
}

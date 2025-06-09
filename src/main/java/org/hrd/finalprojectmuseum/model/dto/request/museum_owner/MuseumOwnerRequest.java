package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hrd.finalprojectmuseum.utils.validateJsonB.ValidJson;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MuseumOwnerRequest {

    private UUID museumCategoryId;

    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9\\s]{1,254}$",
            message = "Name must start with a letter and be 2–255 characters"
    )
    @NotBlank(message = "Name is required")
    private String name;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Must be a valid phone number")
    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot be greater than 255 characters")
    private String address;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Digits(integer = 3, fraction = 8, message = "Latitude must have up to 3 integer digits and 8 fractional digits")
    private BigDecimal lat;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Digits(integer = 4, fraction = 8, message = "Longitude must have up to 4 integer digits and 8 fractional digits")
    private BigDecimal lng;

    @Pattern(
            regexp = "^(https?:\\/\\/.*\\.(?:png|jpg|jpeg|gif))$",
            message = "Must be a valid image URL (jpg, jpeg, png, gif)"
    )
    @NotBlank(message = "Logo link is required")
    private String logoLink;

    @Pattern(
            regexp = "^(https?:\\/\\/.*\\.(?:png|jpg|jpeg|gif))$",
            message = "Must be a valid image URL (jpg, jpeg, png, gif)"
    )
    @NotBlank(message = "Banner link is required")
    private String bannerLink;

    @ValidJson(message = "Key name can not be duplicate and value must be correct link format")
    @NotNull(message = "Landscape link is required")
    private JSONObject landscapeLink;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot be greater than 2000 characters")
    private String description;
}

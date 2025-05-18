package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MuseumOwnerRequest {

    private UUID museumCategoryId;

    @Pattern(
            regexp = "^$|^[a-zA-Z][a-zA-Z0-9\\s]{2,254}$",
            message = "Name must start with a letter and be 2–255 characters"
    )
    private String name;

    @Pattern(regexp = "^$|^\\+?[0-9]{7,15}$", message = "Must be a valid phone number")
    private String contactNumber;
    @Digits(integer = 4, fraction = 6, message = "Must be a number with up to 4 integer digits and 6 fractional digits")
    private BigDecimal lat;
    @Digits(integer = 4, fraction = 6, message = "Must be a number with up to 4 integer digits and 6 fractional digits")
    private BigDecimal lng;
    @Pattern(
            regexp = "^$|^(https?:\\/\\/.*\\.(?:png|jpg|jpeg|gif))$",
            message = "Must be a valid image URL (jpg, jpeg, png, gif) or empty"
    )
    private String logoLink;
    @Pattern(
            regexp = "^$|^(https?:\\/\\/.*\\.(?:png|jpg|jpeg|gif))$",
            message = "Must be a valid image URL (jpg, jpeg, png, gif) or empty"
    )
    private String bannerLink;

    private JSONObject landscapeLink;

    @Pattern(
            regexp = "^$|^[a-zA-Z0-9\\s]{2,2000}$",
            message = "Name must start with a letter and be 2-2000 characters"
    )
    private String description;
}

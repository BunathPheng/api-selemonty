package org.hrd.finalprojectmuseum.model.dto.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hrd.finalprojectmuseum.utils.validateEventDate.ValidateEventDate;
import org.hrd.finalprojectmuseum.utils.validateJsonB.ValidJson;

import java.time.LocalDateTime;

@Data
@ValidateEventDate()
public class EventRequest {
    @NotBlank(message = "Title is required")
    @Length(max = 255, message = "Title is too long. Allowed 1-255 characters")
    private String title;

    @NotBlank(message = "Sub title is required")
    @Length(max = 255, message = "Sub title is too long. Allowed 1-255 characters")
    private String subTitle;

    @NotBlank(message = "Content is required")
    @Length(max = 2000, message = "Content is too long. Allowed 1-2000 characters")
    private String content;

    @Future(message = "Start Date must be in the future")
    @NotNull(message = "Start Date is required")
    private LocalDateTime startDate;

    @Future(message = "End Date must be in the future")
    @NotNull(message = "End Date is required")
    private LocalDateTime endDate;

    @ValidJson(
            maxSize = 1000,
            message = "Invalid image links structure. Allowed only png, jpg, jpeg, gif and glb type!"
    )
    private JSONObject imageLinks;

    @Length(max = 255, message = "Curator is too long. Allowed 1-255 characters")
    private String curator;

    @Length(max = 2000, message = "Accessibility Note is too long. Allowed 1-2000 characters")
    private String accessibilityNote;
}
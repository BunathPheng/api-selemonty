package org.hrd.finalprojectmuseum.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReplyRequest {
    @NotBlank(message = "Reply Text is required")
    @Size(min = 10, max = 1000, message = "Comment must be between 10 and 1000 characters")
    private String replyText;
}

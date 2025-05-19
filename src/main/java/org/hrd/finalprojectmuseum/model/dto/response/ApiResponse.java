package org.hrd.finalprojectmuseum.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T payload;
    private HttpStatus status;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Pagination pagination;
}

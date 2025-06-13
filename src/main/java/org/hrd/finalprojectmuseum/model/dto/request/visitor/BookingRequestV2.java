package org.hrd.finalprojectmuseum.model.dto.request.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.enums.TicketType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingRequestV2 {
    @NotNull(message = "Ticket price cannot be null")
    @Positive(message = "Ticket price must be positive")
    @Digits(integer = 8, fraction = 2, message = "Ticket price must have at most 8 digits and 2 decimal places")
    private BigDecimal ticketPrice;

    @NotNull(message = "Slot amount cannot be null")
    @Min(value = 1, message = "Slot amount must be at least 1")
    @Max(value = 50, message = "Slot amount cannot exceed 50 tickets per booking")
    private Integer slotAmount;

    @NotNull(message = "Booking date cannot be null")
    @Future(message = "Booking date must be in the future")
    private LocalDateTime bookingDate;

    @AssertTrue(message = "Booking date must be at least 1 hour from now")
    private boolean isBookingDateReasonable() {
        if (bookingDate != null) {
            LocalDateTime minimumBookingTime = LocalDateTime.now().plusHours(1);
            return bookingDate.isAfter(minimumBookingTime);
        }
        return true;
    }
}

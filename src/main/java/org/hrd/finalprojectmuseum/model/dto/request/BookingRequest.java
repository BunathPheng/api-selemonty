package org.hrd.finalprojectmuseum.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.enums.BookingType;
import org.hrd.finalprojectmuseum.model.enums.TicketStatus;
import org.hrd.finalprojectmuseum.model.enums.TicketType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingRequest {

    @NotNull(message = "Ticket price cannot be null")
    @Positive(message = "Ticket price must be positive")
    @Digits(integer = 8, fraction = 2, message = "Ticket price must have at most 8 digits and 2 decimal places")
    private BigDecimal ticketPrice;

    @NotNull(message = "Ticket type cannot be null")
    @Schema(description = "Type of ticket", example = "LOCAL")
    private TicketType ticketType;

    @NotNull(message = "Slot amount cannot be null")
    @Min(value = 1, message = "Slot amount must be at least 1")
    @Max(value = 50, message = "Slot amount cannot exceed 50 tickets per booking")
    private Integer slotAmount;

    @NotNull(message = "Booking date cannot be null")
    @Future(message = "Booking date must be in the future")
    private LocalDateTime bookingDate;

    @NotNull(message = "Total price cannot be null")
    @Positive(message = "Total price must be positive")
    @Digits(integer = 10, fraction = 2, message = "Total price must have at most 10 digits and 2 decimal places")
    private BigDecimal totalPrice;

    @AssertTrue(message = "Total price must equal ticket price multiplied by slot amount")
    private boolean isTotalPriceValid() {
        if (ticketPrice != null && slotAmount != null && totalPrice != null) {
            BigDecimal expectedTotal = ticketPrice.multiply(BigDecimal.valueOf(slotAmount));
            return totalPrice.compareTo(expectedTotal) == 0;
        }
        return true;
    }

    @AssertTrue(message = "Booking date must be at least 1 hour from now")
    private boolean isBookingDateReasonable() {
        if (bookingDate != null) {
            LocalDateTime minimumBookingTime = LocalDateTime.now().plusHours(1);
            return bookingDate.isAfter(minimumBookingTime);
        }
        return true;
    }
}
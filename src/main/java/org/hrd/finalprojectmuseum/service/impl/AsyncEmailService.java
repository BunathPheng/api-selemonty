package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrd.finalprojectmuseum.model.entity.Guide;
import org.hrd.finalprojectmuseum.model.entity.visitor.BookingV2;
import org.hrd.finalprojectmuseum.service.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncEmailService {
    private final EmailService emailService;

    @Async("emailExecutor")
    public CompletableFuture<Void> sendBookingConfirmationEmailAsync(BookingV2 booking, String visitorEmail, byte[] qrCodeBytes) throws IOException {
        try {
            log.info("Starting async booking email for booking: {}", booking.getBookingId());
            emailService.sendBookingConfirmationEmail(booking, visitorEmail, qrCodeBytes);
            log.info("Async booking email sent successfully for booking: {}", booking.getBookingId());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to send async booking email for booking: {}", booking.getBookingId(), e);
            throw e;
        }
    }
}


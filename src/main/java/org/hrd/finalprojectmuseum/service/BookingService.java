package org.hrd.finalprojectmuseum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.request.BookingRequest;
import org.hrd.finalprojectmuseum.model.dto.response.BookingDetail;
import org.hrd.finalprojectmuseum.model.dto.response.BookingManagement;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.enums.BookingType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingService {

    Booking makeABookingByMuseumId(UUID museumId, UUID visitorId, BookingRequest bookingRequest);

    List<Booking> getBookingHistoryByVisitorId(UUID visitorId, String search, BookingType category, LocalDate startDate, LocalDate endDate);

    ListResponse<BookingManagement> getAllBookingByMuseumId(UUID museumId, String search, @Min(value = 1, message = "must be greater than 0") Integer page, @Min(value = 1, message = "must be greater than 0") Integer size);

    BookingDetail getBookingHistoryByBookingId(@Valid UUID bookingId, UUID visitorId);

    BookingDetail findScanBookingByBookingId(@NotNull UUID bookingId, UUID museumId);

    BookingDetail findBookingByCodeQr(String codeQr, UUID museumId);
}

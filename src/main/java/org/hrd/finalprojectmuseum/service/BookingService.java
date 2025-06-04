package org.hrd.finalprojectmuseum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.request.BookingRequest;
import org.hrd.finalprojectmuseum.model.dto.request.RequestTourRequest;
import org.hrd.finalprojectmuseum.model.dto.response.BookingDetail;
import org.hrd.finalprojectmuseum.model.dto.response.BookingManagement;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.entity.visitor.BookingV2;
import org.hrd.finalprojectmuseum.model.enums.BookingType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingService {

    Booking makeABookingByMuseumId(UUID museumId, UUID visitorId, BookingRequest bookingRequest);

    ListResponse<Booking> getBookingHistoryByVisitorId(UUID visitorId, String search, Integer page, Integer size, BookingType category, LocalDate startDate, LocalDate endDate);

    ListResponse<Booking> getAllBookingByMuseumId(UUID museumId, String search, Integer page, Integer size, BookingType bookingType, LocalDate startDate, LocalDate endDate);

    Booking getBookingByVisitorId(@Valid UUID bookingId, UUID visitorId);

    BookingV2 getBookingByVisitorIdV2(@Valid UUID bookingId, UUID visitorId);

    Booking findScanBookingByBookingId(@NotNull UUID bookingId, UUID museumId);

    Booking findBookingByCodeQr(String codeQr, UUID museumId);

    Booking getBookingByMuseumId(@Valid UUID bookingId, UUID museumId);

    Booking requestTourByMuseumId(@Valid UUID museumId, UUID visitorId, RequestTourRequest requestTourRequest);
}

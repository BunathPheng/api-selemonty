package org.hrd.finalprojectmuseum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.request.BookingRequest;
import org.hrd.finalprojectmuseum.model.dto.request.RequestTourRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.BookingRequestV2;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.entity.visitor.BookingV2;
import org.hrd.finalprojectmuseum.model.enums.BookingType;
import org.hrd.finalprojectmuseum.model.enums.TicketType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingService {

    List<BookingV2> getVisitorBookingHistory(UUID visitorId, String search, BookingType category, Integer page, Integer size, LocalDate startDate, LocalDate endDate);

    Integer countVisitorBookingHistory(UUID visitorId, String search, BookingType category, Integer page, Integer size, LocalDate startDate, LocalDate endDate);

    List<BookingV2> getMuseumBookingHistory(UUID museumId, String search, Integer page, Integer size);

    Integer countMuseumBookingHistory(UUID museumId, String search);

    BookingV2 getBookingByVisitorIdV2(@Valid UUID bookingId, UUID visitorId);

    BookingV2 bookingIndividualTicket(UUID museumId, UUID visitorId, TicketType ticketType, BookingRequestV2 bookingRequest);

    Booking findScanBookingByBookingId(@NotNull UUID bookingId, UUID museumId);

    Booking findBookingByCodeQr(String codeQr, UUID museumId);

    Booking requestTourByMuseumId(@Valid UUID museumId, UUID visitorId, RequestTourRequest requestTourRequest);
}

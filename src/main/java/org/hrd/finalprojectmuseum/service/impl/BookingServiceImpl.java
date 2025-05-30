package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.BookingRequest;
import org.hrd.finalprojectmuseum.model.dto.request.RequestTourRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.Tour;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.enums.BookingType;
import org.hrd.finalprojectmuseum.repository.BookingRepository;
import org.hrd.finalprojectmuseum.repository.MuseumRepository;
import org.hrd.finalprojectmuseum.repository.TourRepository;
import org.hrd.finalprojectmuseum.service.BookingService;
import org.hrd.finalprojectmuseum.utils.UniqueTextCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UniqueTextCodeGenerator uniqueTextCodeGenerator;
    private final MuseumRepository museumRepository;
    private final TourRepository tourRepository;

    @Override
    public Booking makeABookingByMuseumId(UUID museumId, UUID visitorId, BookingRequest bookingRequest) {
        MuseumOwner museum = museumRepository.findMuseumOwnerByMuseumId(museumId);
        if (museum == null) {
            throw new AppNotFoundException("Museum with id " + museumId + " not exists");
        }
        if (!museum.getIsApproved()) {
            throw new AppBadRequestException("Booking failed. This museum is not approved by admin");
        }

        String code = uniqueTextCodeGenerator.generateUniqueTextCode();
        LocalDateTime expiredDate = bookingRequest.getBookingDate().plusHours(12);
        Booking booking = bookingRepository.insertBookingByMuseumId(museumId, visitorId, code, expiredDate, bookingRequest);

        if (booking == null) {
            throw new AppBadRequestException("Booking failed! Please try again");
        }
        return booking;
    }

    @Override
    public ListResponse<Booking> getBookingHistoryByVisitorId(UUID visitorId, String search, Integer page, Integer size, BookingType category, LocalDate startDate, LocalDate endDate) {
        search = search == null ? "" : search;
        boolean hasBookingType = category != null;
        boolean hasDateRange = startDate != null && endDate != null;

        List<Booking> bookings;
        Integer totalItems;

        // Get bookings with appropriate filters
        if (!hasBookingType && !hasDateRange) {
            bookings = bookingRepository.findByVisitorIdAndSearchWithPagination(visitorId, search.trim(), page, size);
            totalItems = bookingRepository.countByVisitorIdAndSearch(visitorId, search.trim());
        } else if (hasBookingType && !hasDateRange) {
            bookings = bookingRepository.findByVisitorIdSearchAndBookingTypeWithPagination(visitorId, search.trim(), category, page, size);
            totalItems = bookingRepository.countByVisitorIdSearchAndBookingType(visitorId, search.trim(), category);
        } else if (!hasBookingType && hasDateRange) {
            bookings = bookingRepository.findByVisitorIdSearchAndDateRangeWithPagination(visitorId, search.trim(), startDate, endDate, page, size);
            totalItems = bookingRepository.countByVisitorIdSearchAndDateRange(visitorId, search.trim(), startDate, endDate);
        } else {
            bookings = bookingRepository.findByVisitorIdSearchBookingTypeAndDateRangeWithPagination(visitorId, search.trim(), category, startDate, endDate, page, size);
            totalItems = bookingRepository.countByVisitorIdSearchBookingTypeAndDateRange(visitorId, search.trim(), category, startDate, endDate);
        }

        for (Booking booking : bookings) {
            checkAndUpdateExpiration(booking.getBookingId());
            if (category == BookingType.TOUR){
                booking.setTotalPrice(tourRepository.getTourPriceByBookingId(booking.getBookingId()));
            }
        }


        // Calculate pagination
        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);

        return ListResponse.<Booking>builder()
                .items(bookings)
                .pagination(pagination)
                .build();
    }

    @Override
    public ListResponse<Booking> getAllBookingByMuseumId(UUID museumId, String search, Integer page, Integer size, BookingType bookingType, LocalDate startDate, LocalDate endDate) {
        search = search == null ? "" : search;
        boolean hasBookingType = bookingType != null;
        boolean hasDateRange = startDate != null && endDate != null;

        List<Booking> bookings;
        Integer totalItems;

        // Get bookings with appropriate filters
        if (!hasBookingType && !hasDateRange) {
            bookings = bookingRepository.findAllBookingByMuseumIdAndSearchWithPagination(museumId, search.trim(), page, size);
            totalItems = bookingRepository.countAllBookingByMuseumIdAndSearch(museumId, search.trim());
        } else if (hasBookingType && !hasDateRange) {
            bookings = bookingRepository.findAllBookingByMuseumIdSearchAndBookingTypeWithPagination(museumId, search.trim(), bookingType, page, size);
            totalItems = bookingRepository.countAllBookingByMuseumIdSearchAndBookingType(museumId, search.trim(), bookingType);
        } else if (!hasBookingType && hasDateRange) {
            bookings = bookingRepository.findAllBookingByMuseumIdSearchAndDateRangeWithPagination(museumId, search.trim(), startDate, endDate, page, size);
            totalItems = bookingRepository.countAllBookingByMuseumIdSearchAndDateRange(museumId, search.trim(), startDate, endDate);
        } else {
            // hasBookingType && hasDateRange
            bookings = bookingRepository.findAllBookingByMuseumIdSearchBookingTypeAndDateRangeWithPagination(museumId, search.trim(), bookingType, startDate, endDate, page, size);
            totalItems = bookingRepository.countAllBookingByMuseumIdSearchBookingTypeAndDateRange(museumId, search.trim(), bookingType, startDate, endDate);
        }

        // Update expiration status for each booking
        for (Booking booking : bookings) {
            checkAndUpdateExpiration(booking.getBookingId());
        }

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);

        return ListResponse.<Booking>builder()
                .items(bookings)
                .pagination(pagination)
                .build();
    }

    @Override
    public Booking getBookingByVisitorId(UUID bookingId, UUID visitorId) {
        Booking bookingDetail = bookingRepository.findBookingByBookingIdAndVisitorId(bookingId, visitorId);
        if (bookingDetail == null) {
            throw new AppNotFoundException("Booking with id " + bookingId + " not exists");
        }

        checkAndUpdateExpiration(bookingId);
        return bookingDetail;
    }

    @Override
    public Booking findScanBookingByBookingId(UUID bookingId, UUID museumId) {
        Booking bookingDetail = bookingRepository.findBookingByBookingIdAndMuseumId(bookingId, museumId);
        if (bookingDetail == null) {
            throw new AppNotFoundException("Booking with id " + bookingId + " not exists");
        }

        checkAndUpdateExpiration(bookingId);

        if (Objects.equals(bookingDetail.getTicketStatus(), "VALID")) {
            bookingRepository.updateStatus(bookingId, "USED");
        }

        return bookingDetail;
    }

    @Override
    public Booking findBookingByCodeQr(String codeQr, UUID museumId) {
        Booking bookingDetail = bookingRepository.findBookingByCodeQrAndMuseumId(codeQr, museumId);
        if (bookingDetail == null) {
            throw new AppNotFoundException("Code Qr: " + codeQr + " is incorrect");
        }

        checkAndUpdateExpiration(bookingDetail.getBookingId());

        if (Objects.equals(bookingDetail.getTicketStatus(), "VALID")) {
            bookingRepository.updateStatus(bookingDetail.getBookingId(), "USED");
        }

        return bookingDetail;
    }

    @Override
    public Booking getBookingByMuseumId(UUID bookingId, UUID museumId) {
        MuseumOwner museumOwner = museumRepository.findMuseumOwnerByMuseumId(museumId);
        if (museumOwner == null) {
            throw new AppNotFoundException("Museum with id " + museumId + " not exists");
        }
        Booking booking = bookingRepository.findBookingByBookingIdAndMuseumId(bookingId, museumId);
        System.out.println(booking);
        return booking;
    }

    @Transactional
    @Override
    public Booking requestTourByMuseumId(UUID museumId, UUID visitorId, RequestTourRequest requestTourRequest) {
        MuseumOwner museumOwner = museumRepository.findMuseumOwnerByMuseumId(museumId);
        if (museumOwner == null) {
            throw new AppNotFoundException("Museum with id " + museumId + " not exists");
        }
        UUID bookingId = bookingRepository.insertBookingForTourRequest(museumId, visitorId, requestTourRequest);
        tourRepository.insertNewTourRequest(bookingId);
        return bookingRepository.findBookingByBookingIdAndMuseumId(bookingId, museumId);
    }

    public void checkAndUpdateExpiration(UUID bookingId) {
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        if (booking == null) {
            throw new AppNotFoundException("Booking with id " + bookingId + " not exists");
        }

        if (booking.getExpiryDate() != null &&
                LocalDateTime.now().isAfter(booking.getExpiryDate()) &&
                !booking.getTicketStatus().equals("EXPIRED")) {

            booking.setTicketStatus("EXPIRED");
            bookingRepository.updateStatus(bookingId, "EXPIRED");
        }
    }
}
package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.BookingRequest;
import org.hrd.finalprojectmuseum.model.dto.response.BookingDetail;
import org.hrd.finalprojectmuseum.model.dto.response.BookingManagement;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.enums.BookingType;
import org.hrd.finalprojectmuseum.repository.BookingRepository;
import org.hrd.finalprojectmuseum.repository.MuseumRepository;
import org.hrd.finalprojectmuseum.service.BookingService;
import org.hrd.finalprojectmuseum.utils.UniqueTextCodeGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UniqueTextCodeGenerator uniqueTextCodeGenerator;
    private final MuseumRepository museumRepository;

    @Override
    public Booking makeABookingByMuseumId(UUID museumId, UUID visitorId, BookingRequest bookingRequest) {
        MuseumOwner museum = museumRepository.findMuseumOwnerByMuseumId(museumId);
        if (museum == null) {
            throw new AppNotFoundException("Museum with id " + museumId + " not exists");
        }
        String code = uniqueTextCodeGenerator.generateUniqueTextCode();
        LocalDateTime expiredDate = bookingRequest.getBookingDate().plusHours(12);
        Booking booking = bookingRepository.insertBookingByMuseumId(museumId, visitorId, code, expiredDate, bookingRequest);
        if (booking == null) {
            throw new AppBadRequestException("Booking failed! Please try again");
        }
        return booking;
    }

    public List<Booking> getBookingHistoryByVisitorId(UUID visitorId, String search, BookingType bookingType, LocalDate startDate, LocalDate endDate) {
        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasBookingType = bookingType != null;
        boolean hasDateRange = startDate != null && endDate != null;
        List<Booking> bookings = new ArrayList<>();
        if (!hasSearch && !hasBookingType && !hasDateRange) {
            bookings = bookingRepository.findAllByVisitorId(visitorId);
        } else if (hasSearch && !hasBookingType && !hasDateRange) {
            bookings = bookingRepository.findByVisitorIdAndSearch(visitorId, search.trim());
        } else if (!hasSearch && hasBookingType && !hasDateRange) {
            bookings = bookingRepository.findByVisitorIdAndBookingType(visitorId, bookingType);
        } else if (!hasSearch && !hasBookingType && hasDateRange) {
            bookings = bookingRepository.findByVisitorIdAndDateRange(visitorId, startDate, endDate);
        } else if (hasSearch && hasBookingType && !hasDateRange) {
            bookings = bookingRepository.findByVisitorIdSearchAndBookingType(visitorId, search.trim(), bookingType);
        } else if (hasSearch && !hasBookingType && hasDateRange) {
            bookings = bookingRepository.findByVisitorIdSearchAndDateRange(visitorId, search.trim(), startDate, endDate);
        } else if (!hasSearch && hasBookingType && hasDateRange) {
            bookings = bookingRepository.findByVisitorIdBookingTypeAndDateRange(visitorId, bookingType, startDate, endDate);
        } else {
            bookings = bookingRepository.findByVisitorIdSearchBookingTypeAndDateRange(visitorId, search.trim(), bookingType, startDate, endDate);
        }
        for (Booking booking : bookings) {
            checkAndUpdateExpiration(booking.getBookingId());
        }
        return bookings;
    }

    @Override
    public ListResponse<BookingManagement> getAllBookingByMuseumId(UUID museumId, String search, Integer page, Integer size) {
        boolean hasSearch = search != null && !search.trim().isEmpty();
        Integer totalItems;

        List<BookingManagement> bookings;
        if (!hasSearch) {
            bookings = bookingRepository.findAllBookingByMuseumId(museumId, page, size);
            totalItems = bookingRepository.findTotalBooking(museumId, "");
        }else {
            bookings = bookingRepository.findAllBookingByMuseumIdAndSearch(museumId, search, page, size);
            totalItems = bookingRepository.findTotalBooking(museumId, search);
        }
        for (BookingManagement booking : bookings) {
            checkAndUpdateExpiration(booking.getBookingId());
        }
        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);
        return ListResponse.<BookingManagement>builder()
                .items(bookings)
                .pagination(pagination)
                .build();
    }

    @Override
    public BookingDetail getBookingHistoryByBookingId(UUID bookingId, UUID visitorId) {
        BookingDetail bookingDetail = bookingRepository.findBookingByBookingIdAndVisitorId(bookingId, visitorId);
        if (bookingDetail == null) {
            throw new AppNotFoundException("Booking with id " + bookingId + " not exists");
        }

        checkAndUpdateExpiration(bookingId);

        return bookingDetail;
    }

    @Override
    public BookingDetail findScanBookingByBookingId(UUID bookingId, UUID museumId) {
        BookingDetail bookingDetail = bookingRepository.findBookingByBookingIdAndMuseumId(bookingId, museumId);
        if (bookingDetail == null) {
            throw new AppNotFoundException("Booking with id " + bookingId + " not exists");
        }
        checkAndUpdateExpiration(bookingId);
        if (Objects.equals(bookingDetail.getTicketStatus(), "VALID")){
            bookingRepository.updateStatus(bookingId, "USED");
        }
        return bookingDetail;
    }

    @Override
    public BookingDetail findBookingByCodeQr(String codeQr, UUID museumId) {
        BookingDetail bookingDetail = bookingRepository.findBookingByCodeQrAndMuseumId(codeQr, museumId);
        if (bookingDetail == null) {
            throw new AppNotFoundException("Code Qr: " + codeQr + " is incorrect");
        }
        checkAndUpdateExpiration(bookingDetail.getBookingId());
        if (Objects.equals(bookingDetail.getTicketStatus(), "VALID")){
            bookingRepository.updateStatus(bookingDetail.getBookingId(), "USED");
        }
        return bookingDetail;
    }

    public void checkAndUpdateExpiration(UUID bookingId) {
        BookingDetail booking = bookingRepository.findBookingByBookingId(bookingId);
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

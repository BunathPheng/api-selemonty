package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.BookingRequest;
import org.hrd.finalprojectmuseum.model.dto.request.RequestTourRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.TicketInfo;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.BookingV2;
import org.hrd.finalprojectmuseum.model.enums.BookingType;
import org.hrd.finalprojectmuseum.model.enums.TicketType;
import org.hrd.finalprojectmuseum.repository.BookingRepository;
import org.hrd.finalprojectmuseum.repository.MuseumRepository;
import org.hrd.finalprojectmuseum.repository.TicketInfoRepository;
import org.hrd.finalprojectmuseum.repository.TourRepository;
import org.hrd.finalprojectmuseum.service.BookingService;
import org.hrd.finalprojectmuseum.service.TicketInfoService;
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
    private final TicketInfoRepository ticketInfoRepository;
    private final TicketInfoService ticketInfoService;

    @Override
    @Transactional
    public Booking makeABookingByMuseumId(UUID museumId, UUID visitorId, BookingRequest bookingRequest) {
        TicketInfo ticketInfo = ticketInfoRepository.findTicketInfoByMuseumId(museumId);
        if (bookingRequest.getTicketType() == TicketType.LOCAL){
            if (ticketInfo.getLocalPrice().compareTo(bookingRequest.getTicketPrice()) != 0){
                throw new AppBadRequestException("LocalTicket price is wrong. Right LocalTicket price is: "+ ticketInfo.getLocalPrice());
            }
        } else if(bookingRequest.getTicketType() == TicketType.FOREIGNER){
            if (ticketInfo.getForeignPrice().compareTo(bookingRequest.getTicketPrice()) != 0){
                throw new AppBadRequestException("Ticket price is wrong. Right ForeignTicket price is: "+ ticketInfo.getForeignPrice());
            }
        }

        if (ticketInfo.getTotalSlot() < bookingRequest.getSlotAmount()){
            throw new AppBadRequestException("Not enough slots available. Available slots: "+ ticketInfo.getTotalSlot());
        }
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
        booking.setTour(null);
        Integer updateSlot = ticketInfo.getTotalSlot() - bookingRequest.getSlotAmount();
        ticketInfoRepository.updateSlotAmount(museumId, updateSlot);

        if (booking == null) {
            throw new AppBadRequestException("Booking failed! Please try again");
        }
        return booking;
    }

    @Override
    public List<BookingV2> getVisitorBookingHistory(UUID visitorId, String search, BookingType category, Integer page, Integer size, LocalDate startDate, LocalDate endDate) {
        search = search == null ? "" : search;
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;

        boolean hasCategory = category != null;
        boolean hasDateRange = startDate != null && endDate != null;

        List<BookingV2> bookings = null;

        if (!hasCategory && !hasDateRange) {
            bookings = bookingRepository.findVisitorBookingHistoryBySearch(visitorId, search.trim(), page, size);
        } else if (hasCategory && !hasDateRange) {
            bookings = bookingRepository.findVisitorBookingHistoryBySearchAndCategory(visitorId, search.trim(), category, page, size);
        } else if (!hasCategory && hasDateRange) {
            bookings = bookingRepository.findVisitorBookingHistoryBySearchAndDateRange(visitorId, search.trim(), startDate, endDate, page, size);
        } else {
            bookings = bookingRepository.findVisitorBookingHistoryBySearchCategoryAndDateRange(visitorId, search.trim(), category, startDate, endDate, page, size);
        }

        for (BookingV2 booking : bookings) {
            checkAndUpdateExpirationV2(booking.getBookingId());
        }

        return bookings;
    }

    @Override
    public Integer countVisitorBookingHistory(UUID visitorId, String search, BookingType category, Integer page, Integer size, LocalDate startDate, LocalDate endDate) {
        search = search == null ? "" : search;
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;

        boolean hasCategory = category != null;
        boolean hasDateRange = startDate != null && endDate != null;

        Integer totalItems;

        if (!hasCategory && !hasDateRange) {
            totalItems = bookingRepository.countVisitorBookingHistoryBySearch(visitorId, search.trim());
        } else if (hasCategory && !hasDateRange) {
            totalItems = bookingRepository.countVisitorBookingHistoryBySearchAndCategory(visitorId, search.trim(), category);
        } else if (!hasCategory && hasDateRange) {
            totalItems = bookingRepository.countVisitorBookingHistoryBySearchAndDateRange(visitorId, search.trim(), startDate, endDate);
        } else {
            totalItems = bookingRepository.countVisitorBookingHistoryBySearchCategoryAndDateRange(visitorId, search.trim(), category, startDate, endDate);
        }
        return totalItems;
    }

    @Override
    public List<BookingV2> getMuseumBookingHistory(UUID museumId, String search, Integer page, Integer size) {
        search = search == null ? "" : search;

        List<BookingV2> museumHistoryBooking = bookingRepository.getMuseumBookingHistoryByMuseumId(museumId, search, page, size);

//        for (Booking booking : bookings) {
//            checkAndUpdateExpiration(booking.getBookingId());
//            if (category == BookingType.TOUR){
//                booking.setTotalPrice(tourRepository.getTourPriceByBookingId(booking.getBookingId()));
//            }
//        }

        for (BookingV2 booking : museumHistoryBooking) {
            checkAndUpdateExpirationV2(booking.getBookingId());
        }

        return museumHistoryBooking;
    }

    @Override
    public Integer countMuseumBookingHistory(UUID museumId, String search) {
        search = search == null ? "" : search;
        return bookingRepository.countMuseumBookingHistory(museumId, search);
    }

    @Override
    public BookingV2 getBookingByVisitorIdV2(UUID bookingId, UUID visitorId) {
        BookingV2 bookingDetail = bookingRepository.retrieveBookingDetailByVisitorId(bookingId, visitorId);
        if (bookingDetail == null) {
            throw new AppNotFoundException("Booking with id " + bookingId + " not exists");
        }
        checkAndUpdateExpirationV2(bookingId);
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

//        checkAndUpdateExpiration(bookingDetail.getBookingId());

        if (Objects.equals(bookingDetail.getTicketStatus(), "VALID")) {
            bookingRepository.updateStatus(bookingDetail.getBookingId(), "USED");
        }

        return bookingDetail;
    }

    @Transactional
    @Override
    public Booking requestTourByMuseumId(UUID museumId, UUID visitorId, RequestTourRequest requestTourRequest) {
        TicketInfo ticketInfo = ticketInfoRepository.findTicketInfoByMuseumId(museumId);
        if (ticketInfo.getTotalSlot() < requestTourRequest.getSlotAmount()){
            throw new AppBadRequestException("Not enough slots available. Available slots: "+ ticketInfo.getTotalSlot());
        }
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

    public void checkAndUpdateExpirationV2(UUID bookingId) {
        BookingV2 booking = bookingRepository.retrieveBookingByBookingId(bookingId);
        if (booking == null) {
            throw new AppNotFoundException("Booking with id " + bookingId + " not exists");
        }

        if (booking.getExpiredDate() != null &&
                LocalDateTime.now().isAfter(booking.getExpiredDate()) &&
                !booking.getTicketStatus().equals("EXPIRED")) {

            booking.setTicketStatus("EXPIRED");
            bookingRepository.updateStatus(bookingId, "EXPIRED");
        }
    }
}
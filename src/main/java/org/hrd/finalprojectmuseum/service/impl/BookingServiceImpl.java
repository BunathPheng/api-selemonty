package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.BookingRequest;
import org.hrd.finalprojectmuseum.model.dto.request.RequestTourRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.BookingRequestV2;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Booking;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.TicketInfo;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.BookingV2;
import org.hrd.finalprojectmuseum.model.entity.visitor.IndividualBookingInfo;
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
import java.time.ZonedDateTime;
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
    public BookingV2 bookingIndividualTicket(UUID museumId, UUID visitorId, TicketType ticketType, BookingRequestV2 bookingRequest) {
        IndividualBookingInfo individualBookingInfo = bookingRepository.BooingIndividualInfo(museumId);

        if (individualBookingInfo == null) {
            throw new AppNotFoundException("Booking failed. This museum is not approved by admin");
        }
        if (individualBookingInfo.getTicketId() == null) {
            throw new AppBadRequestException("Booking failed. Museum doesn't have ticket information");
        }
        if(!individualBookingInfo.getMuseumId().equals(museumId)) {
            throw new AppNotFoundException("Museum with id " + museumId + " not exists");
        }
        if (ticketType != TicketType.LOCAL && ticketType != TicketType.FOREIGNER) {
            throw new AppBadRequestException("Invalid ticket type for individual booking");
        }
        if (ticketType == TicketType.FOREIGNER){
            if (individualBookingInfo.getForeignPrice().compareTo(bookingRequest.getTicketPrice()) != 0){
                throw new AppBadRequestException("Foreigner Ticket price is wrong. Right LocalTicket price is: "+ individualBookingInfo.getForeignPrice());
            }
        }
        if (ticketType == TicketType.LOCAL) {
            if (individualBookingInfo.getLocalPrice().compareTo(bookingRequest.getTicketPrice()) != 0){
                throw new AppBadRequestException("Local Ticket price is wrong. Right LocalTicket price is: "+ individualBookingInfo.getLocalPrice());
            }
        }

        if (individualBookingInfo.getMuseumSchedule().isEmpty()) {
            throw new AppNotFoundException("Booking failed. Museum doesn't have schedule");
        }

//        LocalDateTime bookingDate = bookingRequest.getBookingDate();
        String bookingDayName = bookingRequest.getBookingDate().getDayOfWeek().name(); // e.g., "MONDAY"

        boolean isClosed = individualBookingInfo.getMuseumSchedule().stream()
                .anyMatch(schedule ->
                        schedule.getDay().equalsIgnoreCase(bookingDayName) && Boolean.TRUE.equals(schedule.getDayOff()));

        if (isClosed) {
            throw new AppBadRequestException("Booking failed. Museum is closed on " + bookingDayName);
        }

        boolean hasNullTimes = individualBookingInfo.getMuseumSchedule().stream()
                .anyMatch(schedule ->
                        schedule.getDay().equalsIgnoreCase(bookingDayName) &&
                                (schedule.getOpeningTime() == null || schedule.getClosingTime() == null));

        if (hasNullTimes) {
            throw new AppBadRequestException("Booking failed. Opening or closing time not available for " + bookingDayName);
        }

        if (individualBookingInfo.getTotalSlots() < bookingRequest.getSlotAmount()) {
            throw new AppBadRequestException("Not enough slots available. Available slots: "+ individualBookingInfo.getTotalSlots());
        }

        String code = uniqueTextCodeGenerator.generateUniqueTextCode();
        LocalDateTime expiredDate = bookingRequest.getBookingDate().plusHours(12);
        BookingV2 booking = bookingRepository.insertBookingIndividual(museumId, visitorId, ticketType, bookingRequest, code, expiredDate);
        Integer updateSlot = individualBookingInfo.getTotalSlots() - bookingRequest.getSlotAmount();
        ticketInfoRepository.updateSlotAmount(museumId, updateSlot);

        if (booking == null) {
            throw new AppBadRequestException("Booking failed! Please try again");
        }
        return bookingRepository.retrieveBookingDetailByVisitorId(booking.getBookingId(), visitorId);
//        return null;
    }

    @Override
    public List<BookingV2> getVisitorBookingHistory(UUID visitorId, String search, BookingType category, Integer page, Integer size, LocalDate startDate, LocalDate endDate) {
        search = search == null ? "" : search;
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;

        boolean hasCategory = category != null;
        boolean hasDateRange = startDate != null && endDate != null;

        List<BookingV2> bookingByVisitorId = getBookingByVisitorId(visitorId);
        for (BookingV2 booking : bookingByVisitorId) {
            checkAndUpdateExpirationV2(booking.getBookingId());
            if (category == BookingType.TOUR){
                booking.setTotalPrice(tourRepository.getTourPriceByBookingId(booking.getBookingId()));
            }
        }

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

        List<BookingV2> bookingByMuseumId = getBookingByMuseumId(museumId);
        for (BookingV2 booking : bookingByMuseumId) {
            checkAndUpdateExpirationV2(booking.getBookingId());
        }
        return bookingRepository.getMuseumBookingHistoryByMuseumId(museumId, search, page, size);
    }

    @Override
    public Integer countMuseumBookingHistory(UUID museumId, String search) {
        search = search == null ? "" : search;
        return bookingRepository.countMuseumBookingHistory(museumId, search);
    }

    @Override
    public BookingV2 getBookingByVisitorIdV2(UUID bookingId, UUID visitorId) {
        String bookingType = checkAndUpdateExpirationV2(bookingId);
        BookingV2 bookingDetail = null;
        if (bookingType.equals("INDIVIDUAL")){
            bookingDetail = bookingRepository.retrieveBookingDetailByVisitorId(bookingId, visitorId);
            if (bookingDetail == null) {
                throw new AppNotFoundException("Booking with id " + bookingId + " not exists");
            }
        } else if (bookingType.equals("TOUR")) {
            bookingDetail = bookingRepository.getBookingByBookingId(bookingId, visitorId);
            if (bookingDetail == null) {
                throw new AppNotFoundException("Booking with id " + bookingId + " not exists");
            }
        }
        return bookingDetail;
    }

    @Override
    public Booking findScanBookingByBookingId(UUID bookingId, UUID museumId) {
        Booking bookingDetail = bookingRepository.findBookingByBookingIdAndMuseumId(bookingId, museumId);
        if (bookingDetail == null) {
            throw new AppNotFoundException("Booking with id " + bookingId + " not exists");
        }

        checkAndUpdateExpiration(bookingId);

        if (Objects.equals(bookingDetail.getTicketStatus(), "USED")) {
            throw new AppNotFoundException("Booking with code " + bookingId + " is already used");
        }

        if (Objects.equals(bookingDetail.getTicketStatus(), "EXPIRED")) {
            throw new AppNotFoundException("Booking with code " + bookingId + " is expired");
        }

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

        if (Objects.equals(bookingDetail.getTicketStatus(), "USED")) {
            throw new AppNotFoundException("Booking with code " + codeQr + " is already used");
        }

        if (Objects.equals(bookingDetail.getTicketStatus(), "EXPIRED")) {
            throw new AppNotFoundException("Booking with code " + codeQr + " is expired");
        }

//        checkAndUpdateExpiration(bookingDetail.getBookingId());

        if (Objects.equals(bookingDetail.getTicketStatus(), "VALID")) {
            bookingRepository.updateStatus(bookingDetail.getBookingId(), "USED");
        }

        return bookingDetail;
    }

    @Transactional
    @Override
    public BookingV2 tourRequest(UUID museumId, UUID visitorId, RequestTourRequest requestTourRequest) {
        IndividualBookingInfo individualBookingInfo = bookingRepository.BooingIndividualInfo(museumId);
        if (individualBookingInfo == null) {
            throw new AppNotFoundException("Booking failed. This museum is not approved by admin");
        }
        if (individualBookingInfo.getTicketId() == null) {
            throw new AppBadRequestException("Booking failed. Museum doesn't have ticket information");
        }
        if(!individualBookingInfo.getMuseumId().equals(museumId)) {
            throw new AppNotFoundException("Museum with id " + museumId + " not exists");
        }
        if (individualBookingInfo.getMuseumSchedule().isEmpty()) {
            throw new AppNotFoundException("Booking failed. Museum doesn't have schedule");
        }

//        LocalDateTime bookingDate = requestTourRequest.getBookingDate();
        String bookingDayName = requestTourRequest.getBookingDate().getDayOfWeek().name(); // e.g., "MONDAY"

        boolean isClosed = individualBookingInfo.getMuseumSchedule().stream()
                .anyMatch(schedule ->
                        schedule.getDay().equalsIgnoreCase(bookingDayName) && Boolean.TRUE.equals(schedule.getDayOff()));

        if (isClosed) {
            throw new AppBadRequestException("Booking failed. Museum is closed on " + bookingDayName);
        }

        boolean hasNullTimes = individualBookingInfo.getMuseumSchedule().stream()
                .anyMatch(schedule ->
                        schedule.getDay().equalsIgnoreCase(bookingDayName) &&
                                (schedule.getOpeningTime() == null || schedule.getClosingTime() == null));

        if (hasNullTimes) {
            throw new AppBadRequestException("Booking failed. Opening or closing time not available for " + bookingDayName);
        }

        UUID bookingId = bookingRepository.insertBookingForTourRequest(museumId, visitorId, requestTourRequest);
        tourRepository.insertNewTourRequest(bookingId);
        return bookingRepository.getBookingByBookingId(bookingId, visitorId);
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

    public String checkAndUpdateExpirationV2(UUID bookingId) {
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
        return booking.getBookingType();
    }

    public List<BookingV2> getBookingByVisitorId(UUID visitorId) {
        return bookingRepository.findBookingByVisitorId(visitorId);
    }

    public List<BookingV2> getBookingByMuseumId(UUID museumId) {
        return bookingRepository.findBookingByMuseumId(museumId);
    }
}
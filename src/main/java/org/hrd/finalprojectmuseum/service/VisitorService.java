package org.hrd.finalprojectmuseum.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.VisitorBooking;
import org.hrd.finalprojectmuseum.model.entity.VisitorBookingDetail;
import org.hrd.finalprojectmuseum.model.entity.VisitorBookingTotal;
import org.hrd.finalprojectmuseum.model.entity.VisitorStat;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;

import java.util.List;
import java.util.UUID;

public interface VisitorService {

    ListResponse<Visitor> getVisitorByUserId(UUID userId, String search, Integer page, Integer size);

    ListResponse<Visitor> getAllVisitor(String search, @Min(value = 1, message = "must be greater than 0") Integer page, @Min(value = 1, message = "must be greater than 0") Integer size);

    Visitor getVisitorById(@NotNull UUID visitorId);

    ListResponse<VisitorBooking> getAllVisitorBooking(String search, Integer page, Integer size);

    ListResponse<VisitorBookingDetail> getBookingByVisitorId(UUID visitorId, String search, Integer page, Integer size);

    VisitorBookingTotal getVisitorBookingTotalByVisitorId(UUID visitorId);

    VisitorStat getVisitorStatByVisitorId();

    List<Visitor> getTopVisitorByMuseumId(UUID museumId);
}

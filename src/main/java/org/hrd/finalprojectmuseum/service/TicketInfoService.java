package org.hrd.finalprojectmuseum.service;

import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.request.TicketInfoRequest;
import org.hrd.finalprojectmuseum.model.entity.TicketInfo;
import org.hrd.finalprojectmuseum.model.entity.TicketStat;

import java.util.UUID;

public interface TicketInfoService {
    TicketInfo getTicketInfoByMuseumId(@NotNull UUID museumId);

    void addTicketInfo(UUID museumId);

    TicketInfo updateTicketInfo(UUID museumId, TicketInfoRequest ticketInfoRequest);

    TicketStat getTicketStat();
}

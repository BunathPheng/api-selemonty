package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.TicketInfoRequest;
import org.hrd.finalprojectmuseum.model.entity.TicketInfo;
import org.hrd.finalprojectmuseum.repository.TicketInfoRepository;
import org.hrd.finalprojectmuseum.service.TicketInfoService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketInfoServiceImpl implements TicketInfoService {

    private final TicketInfoRepository ticketInfoRepository;

    @Override
    public TicketInfo getTicketInfoByMuseumId(UUID museumId) {
        TicketInfo ticketInfo = ticketInfoRepository.findTicketInfoByMuseumId(museumId);
        if (ticketInfo == null) {
            throw new AppNotFoundException("Ticket not found");
        }
        return ticketInfo;
    }

    @Override
    public void addTicketInfo(UUID museumId) {
        ticketInfoRepository.insertTicketInfo(museumId);
    }

    @Override
    public TicketInfo updateTicketInfo(UUID museumId, TicketInfoRequest ticketInfoRequest) {
        return ticketInfoRepository.modifyTicketInfo(museumId, ticketInfoRequest);
    }
}

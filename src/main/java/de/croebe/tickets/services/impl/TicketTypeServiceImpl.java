package de.croebe.tickets.services.impl;

import de.croebe.tickets.domain.entities.*;
import de.croebe.tickets.exceptions.TicketTypeNotFoundException;
import de.croebe.tickets.exceptions.TicketsSoldOutException;
import de.croebe.tickets.exceptions.UserNotFoundException;
import de.croebe.tickets.repositories.TicketRepository;
import de.croebe.tickets.repositories.TicketTypeRepository;
import de.croebe.tickets.repositories.UserRepository;
import de.croebe.tickets.services.QrCodeService;
import de.croebe.tickets.services.TicketTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;

    @Override
    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID ticketTypeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            String message = String.format("User with ID %s not found", userId);
            return new UserNotFoundException(message);
        });

        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticketTypeId).orElseThrow(() -> {
            String message = String.format("Ticket type with ID %s not found", ticketTypeId);
            return new TicketTypeNotFoundException(message);
        });

        int purchasedTicketsCount = ticketRepository.countByTicketTypeId(ticketType.getId());
        Integer totalAvailable = ticketType.getTotalAvailable();

        if (purchasedTicketsCount + 1 > totalAvailable) {
            throw new TicketsSoldOutException("Ticket type is sold out");
        }

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.PURCHASED);
        ticket.setTicketType(ticketType);
        ticket.setPurchaser(user);

        Ticket savedTicket = ticketRepository.save(ticket);
        qrCodeService.generateQrCode(savedTicket);

        return ticketRepository.save(savedTicket);
    }
}

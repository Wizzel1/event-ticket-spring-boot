package de.croebe.tickets.domain.dtos;

import de.croebe.tickets.domain.entities.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GetTicketResponseDto(
        UUID id,
        TicketStatus status,
        BigDecimal price,
        String description,
        String eventName,
        String eventVenue,
        LocalDateTime eventStart,
        LocalDateTime eventEnd
) {
}

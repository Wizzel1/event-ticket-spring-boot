package de.croebe.tickets.domain.dtos;

import de.croebe.tickets.domain.entities.TicketStatus;

import java.util.UUID;

public record ListTicketResponseDto(
        UUID id,
        TicketStatus status,
        ListTicketTicketTypeResponseDto ticketType
) {
}

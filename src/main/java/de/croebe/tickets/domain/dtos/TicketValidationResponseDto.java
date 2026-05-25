package de.croebe.tickets.domain.dtos;

import de.croebe.tickets.domain.entities.TicketValidationStatus;

import java.util.UUID;

public record TicketValidationResponseDto(
        UUID ticketId,
        TicketValidationStatus status
) {
}

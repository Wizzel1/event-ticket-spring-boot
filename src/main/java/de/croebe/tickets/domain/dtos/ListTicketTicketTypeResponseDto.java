package de.croebe.tickets.domain.dtos;

import java.util.UUID;

public record ListTicketTicketTypeResponseDto(
        UUID id,
        String name,
        Double price
) {
}

package de.croebe.tickets.domain.dtos;

import java.util.UUID;

public record ListEventsTicketTypeResponseDto(
        UUID id,
        String name,
        Double price,
        String description,
        Integer totalAvailable
) {
}


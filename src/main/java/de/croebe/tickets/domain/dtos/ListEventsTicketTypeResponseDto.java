package de.croebe.tickets.domain.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record ListEventsTicketTypeResponseDto(
        UUID id,
        String name,
        BigDecimal price,
        String description,
        Integer totalAvailable
) {
}


package de.croebe.tickets.domain.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record ListTicketTicketTypeResponseDto(
        UUID id,
        String name,
        BigDecimal price
) {
}

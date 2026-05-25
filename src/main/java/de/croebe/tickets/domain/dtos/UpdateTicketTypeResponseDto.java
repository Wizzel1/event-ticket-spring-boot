package de.croebe.tickets.domain.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateTicketTypeResponseDto(
        UUID id,
        String name,
        BigDecimal price,
        String description,
        Integer totalAvailable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

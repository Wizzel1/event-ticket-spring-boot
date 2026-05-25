package de.croebe.tickets.domain.dtos;

import java.util.UUID;

public record GetPublishedEventDetailsTicketTypeResponseDto(
        UUID id,
        String name,
        Double price,
        String description
) {
}

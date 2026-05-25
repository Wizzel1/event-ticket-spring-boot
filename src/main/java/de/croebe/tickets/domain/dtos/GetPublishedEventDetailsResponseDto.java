package de.croebe.tickets.domain.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GetPublishedEventDetailsResponseDto(
        UUID id,
        String name,
        LocalDateTime start,
        LocalDateTime end,
        String venue,
        List<GetPublishedEventDetailsTicketTypeResponseDto> ticketTypes
) {
}

package de.croebe.tickets.domain.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record ListPublishedEventsResponseDto(
        UUID id,
        String name,
        LocalDateTime start,
        LocalDateTime end,
        String venue
) {
}

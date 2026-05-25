package de.croebe.tickets.domain.dtos;

import de.croebe.tickets.domain.entities.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateEventResponseDto(
        UUID id,
        String name,
        LocalDateTime start,
        LocalDateTime end,
        String venue,
        LocalDateTime salesStart,
        LocalDateTime salesEnd,
        EventStatus status,
        List<CreateTicketTypeResponseDto> ticketTypes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}


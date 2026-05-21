package de.croebe.tickets.mappers;

import de.croebe.tickets.domain.CreateEventRequest;
import de.croebe.tickets.domain.CreateTicketTypeRequest;
import de.croebe.tickets.domain.dtos.*;
import de.croebe.tickets.domain.entities.Event;
import de.croebe.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);

    ListEventsTicketTypeResponseDto toDto(TicketType ticketType);

    ListEventResponseDto toListEventresponseDto(Event event);
}

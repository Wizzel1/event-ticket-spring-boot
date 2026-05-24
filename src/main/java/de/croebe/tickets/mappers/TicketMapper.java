package de.croebe.tickets.mappers;

import de.croebe.tickets.domain.dtos.GetTicketResponseDto;
import de.croebe.tickets.domain.dtos.ListTicketResponseDto;
import de.croebe.tickets.domain.dtos.ListTicketTicketTypeReponseDto;
import de.croebe.tickets.domain.entities.Ticket;
import de.croebe.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {
    ListTicketTicketTypeReponseDto toListTicketTicketTypeReponseDto(TicketType ticketType);

    ListTicketResponseDto toListTicketResponseDto(Ticket ticket);

    @Mapping(target = "price", source = "ticket.ticketType.price")
    @Mapping(target = "eventName", source = "ticket.ticketType.event.name")
    @Mapping(target = "eventVenue", source = "ticket.ticketType.event.venue")
    @Mapping(target = "eventStart", source = "ticket.ticketType.event.start")
    @Mapping(target = "eventEnd", source = "ticket.ticketType.event.end")
    GetTicketResponseDto toGetTicketResponseDto(Ticket ticket);
}

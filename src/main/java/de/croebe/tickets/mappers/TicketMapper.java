package de.croebe.tickets.mappers;

import de.croebe.tickets.domain.dtos.ListTicketResponseDto;
import de.croebe.tickets.domain.dtos.ListTicketTicketTypeReponseDto;
import de.croebe.tickets.domain.entities.Ticket;
import de.croebe.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {
    ListTicketTicketTypeReponseDto toListTicketTicketTypeReponseDto(TicketType ticketType);

    ListTicketResponseDto toListTicketResponseDto(Ticket ticket);
}

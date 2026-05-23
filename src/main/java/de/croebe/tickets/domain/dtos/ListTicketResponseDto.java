package de.croebe.tickets.domain.dtos;

import de.croebe.tickets.domain.entities.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListTicketResponseDto {
    private UUID id;
    private TicketStatus status;
    private ListTicketTicketTypeReponseDto ticketType;

}

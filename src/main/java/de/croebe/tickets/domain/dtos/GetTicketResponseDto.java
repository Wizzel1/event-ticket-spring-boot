package de.croebe.tickets.domain.dtos;

import de.croebe.tickets.domain.entities.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTicketResponseDto {
    private UUID id;
    private TicketStatus status;
    private Double price;
    private String description;
    private String eventName;
    private String eventVenue;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
}

package de.croebe.tickets.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTicketTypeRequestDto {

    private UUID id;

    @NotBlank(message = "Ticket type name can't be blank")
    private String name;

    @NotNull(message = "Ticket type price cannot be null")
    @PositiveOrZero(message = "Ticket type price cannot be negative")
    private Double price;
    private String description;
    private Integer totalAvailable;
}

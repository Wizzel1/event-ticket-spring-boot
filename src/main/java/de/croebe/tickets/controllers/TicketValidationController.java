package de.croebe.tickets.controllers;

import de.croebe.tickets.domain.dtos.TicketValidationRequestDto;
import de.croebe.tickets.domain.dtos.TicketValidationResponseDto;
import de.croebe.tickets.domain.entities.TicketValidation;
import de.croebe.tickets.domain.entities.TicketValidationMethod;
import de.croebe.tickets.mappers.TicketValidationMapper;
import de.croebe.tickets.services.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/ticket-validations")
@RequiredArgsConstructor
public class TicketValidationController {
    private final TicketValidationService ticketValidationService;
    private final TicketValidationMapper ticketValidationMapper;

    @PostMapping
    public ResponseEntity<TicketValidationResponseDto> validateTicket(
            @RequestBody TicketValidationRequestDto requestDto
    ) {
        TicketValidation validation = switch (requestDto.getMethod()) {
            case QR_CODE -> ticketValidationService.validateTicketByQrCode(requestDto.getId());
            case MANUAL -> ticketValidationService.validateTicketManually(requestDto.getId());
        };
        TicketValidationResponseDto response = ticketValidationMapper.toTicketValidationResponseDto(validation);
        return ResponseEntity.ok(response);
    }
}

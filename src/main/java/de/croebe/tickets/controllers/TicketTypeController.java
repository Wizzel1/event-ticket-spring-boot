package de.croebe.tickets.controllers;

import de.croebe.tickets.services.TicketTypeService;
import de.croebe.tickets.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/events/{eventId}/ticket-types")
public class TicketTypeController {
    private final TicketTypeService ticketTypeService;

    @PostMapping(path = "/{ticketTypeId}/tickets")
    public ResponseEntity<Void> purchaseTicket(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID ticketTypeId
    ) {
        UUID userId = JwtUtil.parseUserId(jwt);
        ticketTypeService.purchaseTicket(userId, ticketTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package de.croebe.tickets.services;

import de.croebe.tickets.domain.entities.TicketValidation;

import java.util.UUID;

public interface TicketValidationService {
    TicketValidation validateTicketByQrCode(UUID qrCodeId);

    TicketValidation validateTicketManually(UUID ticketId);
}

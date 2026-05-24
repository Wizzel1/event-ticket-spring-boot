package de.croebe.tickets.services;


import de.croebe.tickets.domain.entities.QrCode;
import de.croebe.tickets.domain.entities.Ticket;

import java.util.UUID;

public interface QrCodeService {
    QrCode generateQrCode(Ticket ticket);

    byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId);
}

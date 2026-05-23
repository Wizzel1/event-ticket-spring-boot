package de.croebe.tickets.services;


import de.croebe.tickets.domain.entities.QrCode;
import de.croebe.tickets.domain.entities.Ticket;

public interface QrCodeService {
    QrCode generateQrCode(Ticket ticket);
}

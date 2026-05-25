package de.croebe.tickets.services.impl;

import de.croebe.tickets.domain.entities.*;
import de.croebe.tickets.exceptions.QrCodeNotFoundException;
import de.croebe.tickets.exceptions.TicketNotFoundException;
import de.croebe.tickets.repositories.QrCodeRepository;
import de.croebe.tickets.repositories.TicketRepository;
import de.croebe.tickets.repositories.TicketValidationRepository;
import de.croebe.tickets.services.TicketValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketValidationServiceImpl implements TicketValidationService {

    private final QrCodeRepository qrCodeRepository;
    private final TicketValidationRepository validationRepository;
    private final TicketRepository ticketRepository;

    private @NonNull TicketValidation validateTicket(Ticket ticket) {
        TicketValidation validation = TicketValidation
                .builder()
                .ticket(ticket)
                .validationMethod(TicketValidationMethod.QR_CODE)
                .build();

        TicketValidationStatus ticketValidationStatus = ticket
                .getValidationList()
                .stream()
                .filter(v -> TicketValidationStatus.VALID.equals(v.getStatus()))
                .findFirst()
                .map(v -> TicketValidationStatus.INVALID)
                .orElse(TicketValidationStatus.VALID);

        validation.setStatus(ticketValidationStatus);
        return validationRepository.save(validation);
    }

    @Transactional
    @Override
    public TicketValidation validateTicketByQrCode(UUID qrCodeId) {
        QrCode qrCode = qrCodeRepository
                .findByIdAndStatus(qrCodeId, QrCodeStatus.ACTIVE)
                .orElseThrow(() -> {
                    String message = String.format("QR code with ID %s not found", qrCodeId);
                    return new QrCodeNotFoundException(message);
                });

        Ticket ticket = qrCode.getTicket();
        return validateTicket(ticket);
    }


    @Transactional
    @Override
    public TicketValidation validateTicketManually(UUID ticketId) {
        Ticket ticket = ticketRepository
                .findById(ticketId)
                .orElseThrow(() -> {
                    String message = String.format("Ticket with ID %s not found", ticketId);
                    return new TicketNotFoundException(message);
                });
        return validateTicket(ticket);
    }
}

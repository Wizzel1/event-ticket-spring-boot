package de.croebe.tickets.services.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import de.croebe.tickets.domain.entities.QrCode;
import de.croebe.tickets.domain.entities.QrCodeStatus;
import de.croebe.tickets.domain.entities.Ticket;
import de.croebe.tickets.exceptions.QrCodeGenerationException;
import de.croebe.tickets.repositories.QrCodeRepository;
import de.croebe.tickets.services.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private final QRCodeWriter qrCodeWriter;
    private QrCodeRepository qrCodeRepository;

    @Override
    public QrCode generateQrCode(Ticket ticket) {
        try {
            UUID uuid = UUID.randomUUID();
            String base64QrCode = generateQrCodeImage(uuid);
            QrCode qrCode = new QrCode();
            qrCode.setId(uuid);
            qrCode.setStatus(QrCodeStatus.ACTIVE);
            qrCode.setValue(base64QrCode);
            qrCode.setTicket(ticket);
            return qrCodeRepository.saveAndFlush(qrCode);
        } catch (WriterException | IOException ex) {
            throw new QrCodeGenerationException("Failed to generate QR code", ex);
        }

    }

    private String generateQrCodeImage(UUID uuid) throws WriterException, IOException {
        BitMatrix matrix = qrCodeWriter.encode(uuid.toString(), BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

}

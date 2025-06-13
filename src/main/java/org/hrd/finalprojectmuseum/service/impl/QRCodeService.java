package org.hrd.finalprojectmuseum.service.impl;

import com.google.api.client.util.Value;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.hrd.finalprojectmuseum.exception.QRCodeGenerationException;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO; // ✅ ADD THIS IMPORT
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class QRCodeService {
    @Value("${app.qr.size:300}") // ✅ CORRECT @Value ANNOTATION
    private int qrCodeSize;

    public byte[] generateQRCodeFromBookingCode(String bookingCode) {
        try {
            log.info("Generating QR code from booking code: {}", bookingCode);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(bookingCode, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);

            byte[] qrBytes = baos.toByteArray();
            log.info("QR code generated successfully from booking code: {}, size: {} bytes",
                    bookingCode, qrBytes.length);

            return qrBytes;

        } catch (Exception e) {
            log.error("Failed to generate QR code from booking code: {}", bookingCode, e);
            throw new QRCodeGenerationException("Failed to generate QR code", e);
        }
    }
}

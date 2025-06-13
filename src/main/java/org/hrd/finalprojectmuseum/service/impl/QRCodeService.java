package org.hrd.finalprojectmuseum.service.impl;

import com.google.api.client.util.Value;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.hrd.finalprojectmuseum.exception.QRCodeGenerationException;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO; // ✅ ADD THIS IMPORT
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class QRCodeService {
    @Value("${app.qr.size:300}") // ✅ CORRECT @Value ANNOTATION
    private int qrCodeSize;

    public byte[] generateQRCodeFromBookingCode(String bookingCode) {
        try {
            // Use higher error correction for better scanning
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // Highest error correction
            hints.put(EncodeHintType.MARGIN, 1); // Minimal margin for larger QR content
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(bookingCode, BarcodeFormat.QR_CODE, 600, 600, hints);

            // Create high-quality PNG
            BufferedImage qrImage = new BufferedImage(600, 600, BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D graphics = qrImage.createGraphics();

            // Disable anti-aliasing for crisp edges
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, 600, 600);
            graphics.setColor(Color.BLACK);

            for (int x = 0; x < 600; x++) {
                for (int y = 0; y < 600; y++) {
                    if (bitMatrix.get(x, y)) {
                        graphics.fillRect(x, y, 1, 1);
                    }
                }
            }
            graphics.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
}

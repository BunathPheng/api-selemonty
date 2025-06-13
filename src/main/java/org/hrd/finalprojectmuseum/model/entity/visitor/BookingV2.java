package org.hrd.finalprojectmuseum.model.entity.visitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Data
public class BookingV2 {
    private UUID bookingId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID museumId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String museumName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID visitorId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String visitorName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bookingType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime bookingDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ticketType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime purchasedDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal ticketPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer slotAmount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal totalPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String qrCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ticketStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tourStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime expiredDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String museumLogo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private QRCodeData qrCodeData;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class QRCodeData {
        private String base64Image;
        private String dataUrl;
        private String downloadUrl;
        private QRCodeMetadata metadata;

        @Data
        @Builder
        public static class QRCodeMetadata {
            private String format;
            private int width;
            private int height;
            private int sizeBytes;
            private String generatedAt;
            private String qrContent;
        }
    }

    public void setQRCodeData(byte[] qrCodeBytes, String baseUrl) {
        if (qrCodeBytes != null && this.qrCode != null) {
            String base64Image = Base64.getEncoder().encodeToString(qrCodeBytes);
            String dataUrl = "data:image/png;base64," + base64Image;
            String downloadUrl = String.format("%s/api/v1/bookings/%s/qr-code", baseUrl, this.bookingId);

            QRCodeData.QRCodeMetadata metadata = QRCodeData.QRCodeMetadata.builder()
                    .format("PNG")
                    .width(300)
                    .height(300)
                    .sizeBytes(qrCodeBytes.length)
                    .generatedAt(Instant.now().toString())
                    .qrContent(this.qrCode)
                    .build();

            this.qrCodeData = QRCodeData.builder()
                    .base64Image(base64Image)
                    .dataUrl(dataUrl)
                    .downloadUrl(downloadUrl)
                    .metadata(metadata)
                    .build();
        }
    }

    public boolean hasQRCodeData() {
        return this.qrCodeData != null && this.qrCodeData.getBase64Image() != null;
    }
}

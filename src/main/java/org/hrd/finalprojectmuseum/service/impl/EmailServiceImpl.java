package org.hrd.finalprojectmuseum.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrd.finalprojectmuseum.model.entity.EmailDetails;
import org.hrd.finalprojectmuseum.model.entity.Guide;
import org.hrd.finalprojectmuseum.model.entity.visitor.BookingV2;
import org.hrd.finalprojectmuseum.service.EmailService;
import org.hrd.finalprojectmuseum.service.GuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

// Annotation
@Service
// Class
// Implementing EmailService interface
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String sender;
    @Value("${app.name:Museum Booking System}")
    private String appName;

    @Autowired
    private TemplateEngine templateEngine;

    // Method 1
    // To send a simple email
    public String sendSimpleMail(EmailDetails details)
    {

        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            // Sending the mail
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            return "Error while Sending Mail";
        }
    }

    @Override
    public String sendMailAsHTML(String email, String otp) throws IOException {
        EmailDetails details = EmailDetails.builder()
                .recipient(email)
                .subject("Your OTP Verification Code")
                .msgBody(loadTemplate(otp))
                .plainTextBody("Your OTP is: " + otp + ". Please do not share it with anyone.")
                .isHtml(true) // Add this flag to differentiate formats
                .build();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try{
            helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(details.getRecipient());
            helper.setSubject(details.getSubject());
            helper.setText(details.getPlainTextBody(), details.getMsgBody()); // (text, html)

            // Add the QR code image as inline attachment
//            ClassPathResource qrImage = new ClassPathResource("static/ticket-qr.png");
//            helper.addInline("qrcode", qrImage);

            javaMailSender.send(mimeMessage);
            return "Mail Sent Successfully...";
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public String loadTemplate(String otp) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/otpTemplate.html");

        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return content.replace("{{otp}}", otp);
    }

    // Method 2
    // To send an email with attachment
    public String
    sendMailWithAttachment(EmailDetails details)
    {
        // Creating a mime message
        MimeMessage mimeMessage
                = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            // Setting multipart as true for attachments to
            // be send
            mimeMessageHelper
                    = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(
                    details.getSubject());

            // Adding the attachment
            FileSystemResource file
                    = new FileSystemResource(
                    new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(
                    file.getFilename(), file);

            // Sending the mail
            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        }

        // Catch block to handle MessagingException
        catch (MessagingException e) {

            // Display message when exception occurred
            return "Error while sending mail!!!";
        }
    }

    @Override
    public String sendBookingConfirmationEmail(BookingV2 booking, String visitorEmail, byte[] qrCodeBytes) throws IOException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Get museum email from database (as you're currently doing)
            String museumEmail = booking.getMuseumEmail();

            // Send FROM your authenticated email WITH museum branding
            String displayName = String.format("%s via Museum Booking", booking.getMuseumName());
            helper.setFrom(displayName + " <" + sender + ">");

            // Set reply-to museum email (from database)
            if (museumEmail != null && !museumEmail.isEmpty()) {
                helper.setReplyTo(museumEmail);
            }

            helper.setTo(visitorEmail);
            helper.setSubject("Booking Confirmation - " + booking.getMuseumName());

            // Load and process booking template
            String htmlContent = loadBookingTemplate(booking);
            String plainText = buildPlainTextBookingEmail(booking);

            helper.setText(plainText, htmlContent);

            // Add QR code as inline image
            if (qrCodeBytes != null) {
                helper.addInline("qrcode", new ByteArrayResource(qrCodeBytes), "image/png");

                String qrFileName = String.format("booking-%s-qr.png", booking.getQrCode());
                helper.addAttachment(qrFileName, new ByteArrayResource(qrCodeBytes));
            }

            javaMailSender.send(mimeMessage);
            return "Email sent from " + displayName + " with replies going to " + museumEmail;

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send booking confirmation email: " + e.getMessage(), e);
        }
    }

    private String loadBookingTemplate(BookingV2 booking) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/ticketQr.html");
        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // Replace template placeholders

        String qrFileName = String.format("booking-%s-qr.png",
                booking.getQrCode() != null ? booking.getQrCode() : "unknown");

        return content
                .replace("{{appName}}", safeString(appName))
                .replace("{{visitorName}}", safeString(booking.getVisitorName()))
                .replace("{{bookingId}}", safeString(booking.getBookingId()))
                .replace("{{qrCode}}", safeString(booking.getQrCode()))
                .replace("{{qrFileName}}", qrFileName)
                .replace("{{museumName}}", safeString(booking.getMuseumName()))
                .replace("{{museumEmail}}", safeString(booking.getMuseumEmail()))
                .replace("{{museumContactNumber}}", safeString(booking.getMuseumContactNumber()))
                .replace("{{bookingType}}", safeString(booking.getBookingType()))
                .replace("{{ticketType}}", safeString(booking.getTicketType()))
                .replace("{{slotAmount}}", safeString(booking.getSlotAmount()))
                .replace("{{bookingDate}}", formatDate(booking.getBookingDate()))
                .replace("{{totalPrice}}", formatPrice(booking.getTotalPrice()))
                .replace("{{ticketStatus}}", safeString(booking.getTicketStatus()))
                .replace("{{expiredDate}}", formatDate(booking.getExpiredDate()))
                .replace("{{purchasedDate}}", formatDate(booking.getPurchasedDate()))
                .replace("{{ticketPrice}}", formatPrice(booking.getTicketPrice()));
    }


    private String safeString(Object obj) {
        return obj != null ? obj.toString() : "N/A";
    }

    private String formatDate(java.time.LocalDateTime date) {
        if (date == null) return "N/A";
        return date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm"));
    }

    private String formatPrice(java.math.BigDecimal price) {
        if (price == null) return "$0.00";
        return "$" + price.toString();
    }

    private String buildPlainTextBookingEmail(BookingV2 booking) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(booking.getVisitorName()).append(",\n\n");
        body.append("Thank you for booking with ").append(appName).append("!\n\n");
        body.append("Booking Details:\n");
        body.append("Booking ID: ").append(booking.getBookingId()).append("\n");
        body.append("QR Code: ").append(booking.getQrCode()).append("\n");
        body.append("Museum: ").append(booking.getMuseumName()).append("\n");
        body.append("Booking Type: ").append(booking.getBookingType()).append("\n");
        body.append("Ticket Type: ").append(booking.getTicketType()).append("\n");
        body.append("Visit Date: ").append(booking.getBookingDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm"))).append("\n");
        body.append("Number of Tickets: ").append(booking.getSlotAmount()).append("\n");
        body.append("Ticket Price: ").append(booking.getTicketPrice()).append(" each\n");
        body.append("Total Amount: ").append(booking.getTotalPrice()).append("\n");
        body.append("Status: ").append(booking.getTicketStatus()).append("\n");
        body.append("Purchased Date: ").append(booking.getPurchasedDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm"))).append("\n\n");
        body.append("Valid Until: ").append(booking.getExpiredDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm"))).append("\n\n");
        body.append("Important: Please present the attached QR code at the museum entrance.\n");
        body.append("Your QR code: ").append(booking.getQrCode()).append("\n\n");
        body.append("Best regards,\n");
        body.append(appName).append(" Team");

        return body.toString();
    }

    private String buildFallbackPlainTextEmail(BookingV2 booking) {
        StringBuilder body = new StringBuilder();
        body.append("BOOKING CONFIRMATION\n\n");
        body.append("Dear ").append(safeString(booking.getVisitorName())).append(",\n\n");
        body.append("Your booking has been confirmed.\n\n");
        body.append("Booking ID: ").append(safeString(booking.getBookingId())).append("\n");
        body.append("Confirmation Code: ").append(safeString(booking.getQrCode())).append("\n");
        body.append("Museum: ").append(safeString(booking.getMuseumName())).append("\n\n");
        body.append("Please contact us if you need assistance.\n\n");
        body.append("Best regards,\n");
        body.append("The ").append(safeString(appName)).append(" Team");
        return body.toString();
    }

}

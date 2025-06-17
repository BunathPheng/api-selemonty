package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.entity.EmailDetails;
import org.hrd.finalprojectmuseum.model.entity.Guide;
import org.hrd.finalprojectmuseum.model.entity.visitor.BookingV2;

import java.awt.print.Book;
import java.io.IOException;
import java.util.List;

// Interface
public interface EmailService {

    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);

    String sendMailAsHTML(String email, String otp) throws IOException;
    // Method
    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);

    String sendBookingConfirmationEmail(BookingV2 booking, String visitorEmail, byte[] qrCodeBytes) throws IOException;

//    String sendBookingConfirmationEmail(BookingV2 booking, List<Guide> guides, String visitorEmail, byte[] qrCodeBytes) throws IOException;
}

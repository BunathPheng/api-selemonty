package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.entity.EmailDetails;

// Interface
public interface EmailService {

    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);

    String sendMailAsHTML(String email, String otp);
    // Method
    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);
}

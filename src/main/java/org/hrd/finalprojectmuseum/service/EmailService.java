package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.entity.EmailDetails;

import java.io.IOException;

// Interface
public interface EmailService {

    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);

    String sendMailAsHTML(String email, String otp) throws IOException;
    // Method
    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);
}

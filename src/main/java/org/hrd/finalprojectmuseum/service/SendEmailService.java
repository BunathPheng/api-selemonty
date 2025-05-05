package org.hrd.finalprojectmuseum.service;

import java.io.IOException;

public interface SendEmailService {
    String generateOtp();

    void sendOtpEmail(String toEmail, String otp) throws IOException;
     void sendResetPasswordEmail(String toEmail, String resetLink) throws IOException;
    String loadTemplate(String otp) throws IOException;
}

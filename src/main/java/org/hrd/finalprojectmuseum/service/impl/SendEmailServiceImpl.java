package org.hrd.finalprojectmuseum.service.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.service.SendEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Service
public class SendEmailServiceImpl implements SendEmailService {

    //    @Value("${sendgrid.api.key}")
    @Value("${app.api.sendgrid}")
   String sendGridApiKey;

    public String generateOtp() {
        int otp = 100000 + new Random().nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOtpEmail(String toEmail, String otp) throws IOException {
        Email from = new Email("tirachlo34@gmail.com", "Sela Monty Team");
        String subject = "Your OTP Code: " + otp;
        Email to = new Email(toEmail);
        Content content = new Content("text/html", loadTemplate(otp));
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
        } catch (IOException ex) {
            throw new AppNotFoundException("Failed to send OTP: " + ex.getMessage());
        }
    }

    public void sendResetPasswordEmail(String toEmail, String resetLink) throws IOException {
//        Email from = new Email("hoeunpichet@gmail.com", "Anusa Boran Company");
//        String subject = "Reset Your Password";
//        Email to = new Email(toEmail);
//        Content content = new Content("text/html", loadResetTemplate(resetLink));
//        Mail mail = new Mail(from, subject, to, content);
//
//        SendGrid sg = new SendGrid(sendGridApiKey);
//        Request request = new Request();
//        try {
//            request.setMethod(Method.POST);
//            request.setEndpoint("mail/send");
//            request.setBody(mail.build());
//            Response response = sg.api(request);
//        } catch (IOException ex) {
//            throw new AppNotFoundException("Failed to send reset email: " + ex.getMessage());
//        }
    }


    public String loadTemplate(String otp) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/otpTemplate.html");
        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return content.replace("{{otp}}", otp);
    }

//    public String loadResetTemplate(String resetLink) throws IOException {
//        ClassPathResource resource = new ClassPathResource("templates/resetPasswordTemplate.html");
//        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//        return content.replace("{{resetLink}}", resetLink);
//    }


}

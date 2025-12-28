package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailTestController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/test")
    public ResponseEntity<String> testEmail(@RequestBody Map<String, String> request) {
        try {
            String toEmail = request.get("to");
            String subject = request.get("subject");
            String message = request.get("message");

            if (toEmail == null || toEmail.isEmpty()) {
                return ResponseEntity.badRequest().body("To email is required");
            }

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toEmail);
            mailMessage.setSubject(subject != null ? subject : "Test Email from ComputerSeekho");
            mailMessage.setText(message != null ? message : "This is a test email to verify email functionality.");

            System.out.println("Attempting to send test email to: " + toEmail);
            mailSender.send(mailMessage);
            System.out.println("Test email sent successfully to: " + toEmail);

            return ResponseEntity.ok("Test email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Error sending test email: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error sending email: " + e.getMessage());
        }
    }

    @PostMapping("/simple-test")
    public ResponseEntity<String> simpleTestEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("gwaliorkt09@gmail.com"); // Send to yourself for testing
            message.setSubject("Email Test - ComputerSeekho");
            message.setText("Hello!\n\nThis is a test email to verify that your email configuration is working properly.\n\nBest regards,\nComputerSeekho Team");

            System.out.println("Attempting to send simple test email");
            mailSender.send(message);
            System.out.println("Simple test email sent successfully");

            return ResponseEntity.ok("Simple test email sent successfully");
        } catch (Exception e) {
            System.err.println("Error sending simple test email: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error sending email: " + e.getMessage());
        }
    }
}

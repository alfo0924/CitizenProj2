package org.example._citizenproj2.service;

public interface EmailService {
    void sendPasswordResetEmail(String email, String resetToken);
    void sendWelcomeEmail(String email);
}
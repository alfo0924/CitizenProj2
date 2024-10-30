package org.example._citizenproj2.model;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private static final String FROM_EMAIL = "noreply@citizencard.com";

    @Override
    public void sendPasswordResetEmail(String email, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(email);
        message.setSubject("密碼重設請求");
        message.setText("您的密碼重設連結：\n" +
                "http://localhost:5173/reset-password?token=" + resetToken + "\n" +
                "此連結將在24小時後失效。");

        mailSender.send(message);
    }

    @Override
    public void sendWelcomeEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(email);
        message.setSubject("歡迎加入市民卡");
        message.setText("感謝您註冊市民卡會員！\n" +
                "請開始使用我們提供的各項服務。");

        mailSender.send(message);
    }
}
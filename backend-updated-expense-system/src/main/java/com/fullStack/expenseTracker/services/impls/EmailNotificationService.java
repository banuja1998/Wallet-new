package com.fullStack.expenseTracker.services.impls;

import com.fullStack.expenseTracker.models.User;
import com.fullStack.expenseTracker.services.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailNotificationService implements NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    private static final String SENDER_NAME = "Expense Tracker";

    // -------------------------------
    // USER REGISTRATION EMAIL
    // -------------------------------
    @Override
    public void sendUserRegistrationVerificationEmail(User user)
            throws MessagingException, UnsupportedEncodingException {

        sendVerificationEmail(
                user,
                "Please verify your registration",
                buildRegistrationContent(user)
        );
    }

    // -------------------------------
    // FORGOT PASSWORD EMAIL
    // -------------------------------
    public void sendForgotPasswordVerificationEmail(User user)
            throws MessagingException, UnsupportedEncodingException {

        sendVerificationEmail(
                user,
                "Forgot Password - Verification Code",
                buildForgotPasswordContent(user)
        );
    }

    // -------------------------------
    // COMMON EMAIL SENDER (REUSABLE)
    // -------------------------------
    private void sendVerificationEmail(User user, String subject, String content)
            throws MessagingException, UnsupportedEncodingException {

        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User email cannot be null");
        }

        MimeMessage message = javaMailSender.createMimeMessage();

        // IMPORTANT: true = multipart support
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromMail, SENDER_NAME);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(content, true); // HTML enabled

        javaMailSender.send(message);
    }

    // -------------------------------
    // EMAIL TEMPLATE - REGISTRATION
    // -------------------------------
    private String buildRegistrationContent(User user) {
        return """
                <html>
                <body>
                    <h3>Welcome, %s!</h3>

                    <p>Thank you for joining us. We are happy to have you on board.</p>

                    <p>To complete your registration, use the verification code below:</p>

                    <h2 style="color:#2E86C1;">%s</h2>

                    <p><b>Note:</b> This code will expire in 15 minutes.</p>

                    <br>
                    <p>Thanks,<br><b>Expense Tracker Team</b></p>
                </body>
                </html>
                """.formatted(user.getUsername(), user.getVerificationCode());
    }

    // -------------------------------
    // EMAIL TEMPLATE - FORGOT PASSWORD
    // -------------------------------
    private String buildForgotPasswordContent(User user) {
        return """
                <html>
                <body>
                    <h3>Password Reset Request</h3>

                    <p>Hello %s,</p>

                    <p>Use the following verification code to reset your password:</p>

                    <h2 style="color:#E74C3C;">%s</h2>

                    <p><b>Important:</b> This code expires in 15 minutes.</p>

                    <br>
                    <p>Thanks,<br><b>Expense Tracker Team</b></p>
                </body>
                </html>
                """.formatted(user.getUsername(), user.getVerificationCode());
    }
}
package com.example.Rams.services;

import com.example.Rams.models.ContactRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.email.to}")
    private String toEmail;

    @Value("${app.email.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async("taskExecutor")
    public void sendContactRequest(ContactRequest request) {
        long startTime = System.currentTimeMillis();
        logger.info("🚀 Начало асинхронной отправки заявки от: {}", request.getName());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setFrom(fromEmail);
            message.setSubject("🚨 Новая заявка с сайта РАМС: " + request.getName());
            message.setText(createEmailText(request));

            mailSender.send(message);

            long endTime = System.currentTimeMillis();
            logger.info("✅ Письмо успешно отправлено за {} мс! Получатель: {}",
                    (endTime - startTime), toEmail);

        } catch (Exception e) {
            logger.error("❌ Ошибка отправки письма для заявки от {}: {}",
                    request.getName(), e.getMessage());
        }
    }

    private String createEmailText(ContactRequest request) {
        return String.format(
                "🚨 НОВАЯ ЗАЯВКА С САЙТА РАМС\n\n" +
                        "📋 Имя: %s\n" +
                        "📞 Телефон: %s\n" +
                        "📧 Email: %s\n" +
                        "💬 Сообщение: %s\n\n" +
                        "⏰ Дата и время: %s\n" +
                        "🔗 Отправлено с: hughes2905@yandex.ru",
                request.getName(),
                request.getPhone(),
                request.getEmail() != null ? request.getEmail() : "Не указан",
                request.getMessage(),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
        );
    }
}
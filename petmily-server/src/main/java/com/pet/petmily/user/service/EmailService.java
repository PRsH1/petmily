package com.pet.petmily.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Async
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("[Petmily] 이메일 인증을 완료해주세요");
            message.setText(
                    "안녕하세요! Petmily입니다.\n\n" +
                    "아래 링크를 클릭하여 이메일 인증을 완료해주세요.\n" +
                    "인증 링크는 24시간 동안 유효합니다.\n\n" +
                    baseUrl + "/sign-up/verify-email?token=" + token + "\n\n" +
                    "본인이 가입하지 않으셨다면 이 메일을 무시해주세요."
            );
            mailSender.send(message);
            log.info("인증 메일 발송 완료: {}", toEmail);
        } catch (Exception e) {
            log.error("인증 메일 발송 실패: {} - {}", toEmail, e.getMessage());
        }
    }
}

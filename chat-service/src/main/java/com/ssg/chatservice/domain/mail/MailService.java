package com.ssg.chatservice.domain.mail;//package com.Gritty.Linki.util.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendPostNotification(String to, String message) {

        PostDTO post = PostDTO.builder()
                .title("LINKI 알림")
                .content(message)
                .author("Linki notification")
                .template("mail/Notification")
                .createdAt(LocalDateTime.now())
                .writeId(1)
                .build();

        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(post.getTitle());
            Context ctx = new Context();
            ctx.setVariable("post", post);
            String html = templateEngine.process(post.getTemplate(), ctx);
            helper.setText(html, true);
            mailSender.send(mime);
            log.info("메일 전송 완료: to={}, title={}", to, post.getTitle());
        } catch (MessagingException e) {
            e.printStackTrace(); // or use a logger
        }
    }


}

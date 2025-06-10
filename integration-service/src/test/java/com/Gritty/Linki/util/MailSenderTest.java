package com.Gritty.Linki.util;

import com.Gritty.Linki.util.mail.MailService;
import com.Gritty.Linki.util.mail.PostDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailSenderTest {

    @Autowired
    private MailService mailSender;

    @Test
    public void testSendMail() {
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Linki Mail Test Title");
        postDTO.setTemplate("mail/newPost");

        mailSender.sendPostNotification("sinminhyeok@gmail.com",postDTO);
    }
}

package com.linki.chatbot_service.controller;

import com.linki.chatbot_service.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/v1/chatbot/request")
    public ResponseEntity<String> chatbot(@RequestBody String msg) {
        log.info("Chatbot message received: " + msg);

        return ResponseEntity.ok(chatbotService.sendMessage(msg));
    }
}

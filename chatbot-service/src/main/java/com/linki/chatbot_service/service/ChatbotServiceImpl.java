package com.linki.chatbot_service.service;

import com.linki.chatbot_service.util.GptClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatbotServiceImpl implements ChatbotService {

    private final GptClient gptClient;

    @Override
    public String sendMessage(String msg) {
        String result = gptClient.request("GPT/chatbot.json",msg);
        log.info("Chatbot message sent: " + result);
        return result;
    }
}

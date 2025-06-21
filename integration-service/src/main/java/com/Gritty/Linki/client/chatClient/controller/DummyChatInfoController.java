package com.Gritty.Linki.client.chatClient.controller;

import com.Gritty.Linki.client.chatClient.dto.respone.ChatInfoResponse;
import com.Gritty.Linki.client.chatClient.dto.respone.PartnerInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/v1/integration-service/api")
public class DummyChatInfoController {
    @GetMapping("/partners/{id}")
    public PartnerInfoResponse getPartnerInfo(@PathVariable String id) {
        return new PartnerInfoResponse(id,"test","test","test","test","PENDING");
    }

    @GetMapping("/chatInfo/{id}")
    public List<ChatInfoResponse> getChatInfo(@PathVariable String id) {
        return  List.of(
                new ChatInfoResponse("test1", "test1", "PRP-0000000000000002"),
                new ChatInfoResponse("test2", "test2", "PRP-0000000000000003")
        );
    }

    @GetMapping("/user-chat")
    public List<ChatInfoResponse> getUserChatInfo(String authorization) {
        return List.of(
                new ChatInfoResponse("test1", "test1", "PRP-0000000000000002"),
                new ChatInfoResponse("test2", "test2", "PRP-0000000000000003")
        );
    }
}

package com.Gritty.Linki.client.chatClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/v1/integration-service/api/chatInfo")
public class DummyChatInfoController {

    @GetMapping("/{id}")
    public List<ChatInfoResponse> getChatInfo(@PathVariable String id) {
        return  List.of(
                new ChatInfoResponse("test1", "test1", "PRP-0000000000000002"),
                new ChatInfoResponse("test2", "test2", "PRP-0000000000000003")
        );
    }
}

package com.ssg.chatservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckController {
    @GetMapping
    @RequestMapping("/v1/chat-service/api/check")
    public String check() {
        return "OK";
    }
}

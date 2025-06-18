package com.Gritty.Linki.client.chatClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/integration-service/api/partners")
public class DummyPartnerController {

    @GetMapping("/{id}")
    public PartnerInfoResponse getPartnerInfo(@PathVariable String id) {
        return new PartnerInfoResponse(id,"test","test","test","test","PENDING");
    }
}

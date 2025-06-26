package com.Gritty.Linki.domain.user.influencer.contract.webhook;

import com.Gritty.Linki.domain.user.influencer.dto.UcanSignWebhookDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class UcanSignWebhookController {

    private final ContractWebhookService contractWebhookService;

    @PostMapping("v1/api/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody UcanSignWebhookDTO dto) {
        log.info("📩 유캔사인 웹훅 수신: {}", dto);
        contractWebhookService.handleSignatureEvent(dto);
        return ResponseEntity.ok().build();

    }
}

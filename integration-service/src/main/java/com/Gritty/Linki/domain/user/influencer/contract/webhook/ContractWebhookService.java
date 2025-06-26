package com.Gritty.Linki.domain.user.influencer.contract.webhook;

import com.Gritty.Linki.domain.user.influencer.dto.UcanSignWebhookDTO;

public interface ContractWebhookService {
    void handleSignatureEvent(UcanSignWebhookDTO signWebhookDTO);
}

package com.Gritty.Linki.util;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Log4j2
public class IdGeneratorTest {


    @Test
    @DisplayName("ID Generator Test")
    public void testIdGenerationOutput() {
        String userId = IdGenerator.userId();
        String authId = IdGenerator.authId();
        String statsId = IdGenerator.statsId();
        String channelId = IdGenerator.channelId();
        String influencerId = IdGenerator.influencerId();
        String advertiserId = IdGenerator.advertiserId();
        String campaignId = IdGenerator.campaignId();
        String proposalId = IdGenerator.proposalId();
        String contractId = IdGenerator.contractId();
        String signatureId = IdGenerator.signatureId();
        String influencerReviewId = IdGenerator.influencerReviewId();
        String advertiserReviewId = IdGenerator.advertiserReviewId();
        String adminId = IdGenerator.adminId();
        String chatId = IdGenerator.chatId();
        String chatAlarmId = IdGenerator.chatAlarmId();
        String subscribeId = IdGenerator.subscribeId();
        String paymentId = IdGenerator.paymentId();
        String billingId = IdGenerator.billingId();
        String refundId = IdGenerator.refundId();

        log.info("userId = {}", userId);
        log.info("authId = {}", authId);
        log.info("statsId = {}", statsId);
        log.info("channelId = {}", channelId);
        log.info("influencerId = {}", influencerId);
        log.info("advertiserId = {}", advertiserId);
        log.info("campaignId = {}", campaignId);
        log.info("proposalId = {}", proposalId);
        log.info("contractId = {}", contractId);
        log.info("signatureId = {}", signatureId);
        log.info("influencerReviewId = {}", influencerReviewId);
        log.info("advertiserReviewId = {}", advertiserReviewId);
        log.info("adminId = {}", adminId);
        log.info("chatId = {}", chatId);
        log.info("chatAlarmId = {}", chatAlarmId);
        log.info("subscribeId = {}", subscribeId);
        log.info("paymentId = {}", paymentId);
        log.info("billingId = {}", billingId);
        log.info("refundId = {}", refundId);
    }
}

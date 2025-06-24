    package com.ssg.chatservice.client;

    import org.springframework.cloud.openfeign.FeignClient;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestHeader;

    import java.util.List;

    @FeignClient(name="chatInfo-api",url="http://localhost:8000")
    public interface ChatApiClient {

        @GetMapping("/v1/integration-service/api/partners/{proposalId}")
        PartnerInfoResponse getPartnerInfo(
                @RequestHeader("Authorization") String authorization,
                @PathVariable("proposalId") String proposalId);

        @GetMapping("/v1/integration-service/api/chatInfo/{campaignId}")
        List<ChatInfoResponse> getChatInfo(
                @RequestHeader("Authorization") String authorization,
                @PathVariable("campaignId") String campaignId);

        @GetMapping("/v1/integration-service/api/user-chat")
        List<ChatInfoResponse> getUserChatInfo(
                @RequestHeader("Authorization") String authorization);


    }
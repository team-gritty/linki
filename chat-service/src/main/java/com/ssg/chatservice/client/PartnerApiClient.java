    package com.ssg.chatservice.client;

    import lombok.extern.slf4j.Slf4j;
    import org.springframework.cloud.openfeign.FeignClient;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestHeader;

    @FeignClient(name="partner-api",url="http://localhost:8080")
    public interface PartnerApiClient{
        @GetMapping("/v1/integration-service/api/partners/{id}")
        PartnerInfoResponse getPartnerInfo(
                @RequestHeader("Authorization") String authorization,
                @PathVariable("id") String proposalId);
    }

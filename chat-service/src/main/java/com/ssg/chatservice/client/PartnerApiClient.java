    package com.ssg.chatservice.client;

    import org.springframework.cloud.openfeign.FeignClient;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;

    @FeignClient(name="partner-api",url="http://localhost:8080")
    public interface PartnerApiClient{
        @GetMapping("/v1/integration-service/api/partners/{id}")
        PartnerInfoResponse getPartnerInfo(@PathVariable("id") String partnerId);
    }

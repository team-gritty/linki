package com.ssg.subscribeservice.controller;

import com.ssg.subscribeservice.dto.responseDto.BillingRegisteredDto;
import com.ssg.subscribeservice.service.SubscribeService;
import com.ssg.subscribeservice.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/subscribe-service/api")
@RequiredArgsConstructor
public class SubscribeController {

    private final SecurityUtil securityUtil;
    private final SubscribeService subscribeService;

    @PostMapping("/subscribe")
    public void confirmSubscribe(){
        String userId = securityUtil.getCurrentUserId();
        String role = securityUtil.getCurrentUserRole();
    }

    @GetMapping("/subscribe/billing-registered")
    public ResponseEntity<BillingRegisteredDto> isBillingRegistered(){
        BillingRegisteredDto billingRegisteredDto = BillingRegisteredDto.builder()
                .billingRegistered(subscribeService.isBillingRegistered(securityUtil.getCurrentUserId()))
                .build();
        return ResponseEntity.ok(billingRegisteredDto);
    }





//    private final SubscribeService subscribeService;
//    private final SubscriptionProducer subscriptionProducer;
//    @PostMapping("subscribe")
//    public void subscribe(@RequestBody SubscribeRequest r){
//        SubscribePlanEntity plan = planRepo.findById(r.subscribeCode())
//                .orElseThrow();
//        // DB에 UserSubscriptionEntity(status=CREATING) 저장
//        prod.sendCreated(r.userId(), plan.getSubscribeCode());
//    }
}

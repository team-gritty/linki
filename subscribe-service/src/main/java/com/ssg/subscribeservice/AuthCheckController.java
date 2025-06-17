package com.ssg.subscribeservice;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/subscribe-service/api/influencer")
public class AuthCheckController {

    /** ② Principal 로 주입 (위 Converter 결과) */
    @GetMapping("/check")
    public String check2(@AuthenticationPrincipal String userId,
                         Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        return "influencer " + userId + " " + role;
    }
}

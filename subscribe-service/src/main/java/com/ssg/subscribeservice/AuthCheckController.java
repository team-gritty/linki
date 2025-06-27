package com.ssg.subscribeservice;

import com.ssg.subscribeservice.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/subscribe-service/api/influencer")
@RequiredArgsConstructor
public class AuthCheckController {
    private final SecurityUtil securityUtil;

    /** ② Principal 로 주입 (위 Converter 결과) */
    @GetMapping("/check")
    public String check2(@AuthenticationPrincipal String userId,
                         Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        String role2 = securityUtil.getCurrentUserRole();
        String userId2 = securityUtil.getCurrentUserId();
        return "influencer " + userId + role2 + " " + role + userId2;
    }
}

package com.Gritty.Linki;


import com.Gritty.Linki.config.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("v1/api/user")
@RestController
@Slf4j
public class InfluencerCheckController {
    @RequestMapping("/check")
    public String check(@AuthenticationPrincipal CustomUserDetails user) {
        String id   = user.getUserId();                         // PK
        String role = user.getAuthorities().iterator().next()
                .getAuthority();

        return "influencer " + id + " " + role;
    }
}

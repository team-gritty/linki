package com.Gritty.Linki;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("v1/api/advertiser")
@RestController
public class AdvertiserCheckController {
    @RequestMapping("/check")
    public String check() {
        return "advertiser";
    }
}

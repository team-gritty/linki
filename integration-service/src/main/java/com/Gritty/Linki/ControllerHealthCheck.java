package com.Gritty.Linki;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api")
public class ControllerHealthCheck {
    @GetMapping("welcome")
    public String welcome() {
        return "Welcome to Linki";
    }
}

package com.Gritty.Linki.config.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class InfoContributor implements org.springframework.boot.actuate.info.InfoContributor {
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("app", Map.of(
                        "name",        "integration-service",
                        "version",     "1.0.0",
                        "description", "integration microservice for Linki platform"))
                .withDetail("company", "SSG Inc.")
                .withDetail("contact", Map.of(
                        "email", "aaaaaa@ssg.com"))
                .withDetail("deploy", Map.of(
                        "env", "prod"));
    }
}

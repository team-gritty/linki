package com.linki.admin_integration_service.domain.dashboard.controller;

import com.linki.admin_integration_service.domain.dashboard.dto.DashboardResponseDTO;
import com.linki.admin_integration_service.domain.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/v1/admin/api/dashboard")
    public ResponseEntity<DashboardResponseDTO> dashboard() {
        return ResponseEntity.ok().body(dashboardService.dashboard());
    }

}

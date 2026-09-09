package com.finsense.backend.insights;

import com.finsense.backend.insights.dto.InsightsResponse;
import com.finsense.backend.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/insights")
public class InsightsController {

    private final InsightsService insightsService;

    public InsightsController(InsightsService insightsService) {
        this.insightsService = insightsService;
    }

    @GetMapping
    public ResponseEntity<InsightsResponse> getInsights(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(insightsService.getInsights(principal.getId()));
    }
}

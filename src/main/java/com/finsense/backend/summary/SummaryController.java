package com.finsense.backend.summary;

import com.finsense.backend.security.UserPrincipal;
import com.finsense.backend.summary.dto.SummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping
    public ResponseEntity<SummaryResponse> getSummary(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(summaryService.getSummary(principal.getId()));
    }
}

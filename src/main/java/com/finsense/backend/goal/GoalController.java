package com.finsense.backend.goal;

import com.finsense.backend.goal.dto.CreateGoalRequest;
import com.finsense.backend.goal.dto.DepositRequest;
import com.finsense.backend.goal.dto.GoalResponse;
import com.finsense.backend.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> list(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(goalService.listForUser(principal.getId()));
    }

    @PostMapping
    public ResponseEntity<GoalResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateGoalRequest request
    ) {
        GoalResponse response = goalService.create(principal.getUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<GoalResponse> deposit(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody DepositRequest request
    ) {
        return ResponseEntity.ok(goalService.deposit(principal.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id
    ) {
        goalService.delete(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}

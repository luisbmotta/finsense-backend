package com.finsense.backend.user;

import com.finsense.backend.security.UserPrincipal;
import com.finsense.backend.user.dto.UpdateMonthlyIncomeRequest;
import com.finsense.backend.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getCurrentUser(principal.getId()));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateMonthlyIncome(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateMonthlyIncomeRequest request
    ) {
        return ResponseEntity.ok(userService.updateMonthlyIncome(principal.getId(), request));
    }
}

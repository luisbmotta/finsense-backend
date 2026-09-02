package com.finsense.backend.user;

import com.finsense.backend.common.exception.ResourceNotFoundException;
import com.finsense.backend.user.dto.UpdateMonthlyIncomeRequest;
import com.finsense.backend.user.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        User user = findUserOrThrow(userId);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateMonthlyIncome(UUID userId, UpdateMonthlyIncomeRequest request) {
        User user = findUserOrThrow(userId);
        user.setMonthlyIncome(request.monthlyIncome());
        user = userRepository.save(user);
        return UserResponse.from(user);
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + userId));
    }
}

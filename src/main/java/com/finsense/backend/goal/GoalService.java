package com.finsense.backend.goal;

import com.finsense.backend.common.exception.ResourceNotFoundException;
import com.finsense.backend.goal.dto.CreateGoalRequest;
import com.finsense.backend.goal.dto.DepositRequest;
import com.finsense.backend.goal.dto.GoalResponse;
import com.finsense.backend.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> listForUser(UUID userId) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(GoalResponse::from)
                .toList();
    }

    @Transactional
    public GoalResponse create(User user, CreateGoalRequest request) {
        Goal goal = new Goal(
                user,
                request.name(),
                request.targetAmount(),
                request.emoji(),
                request.deadline(),
                request.color()
        );

        goal = goalRepository.save(goal);
        return GoalResponse.from(goal);
    }

    @Transactional
    public GoalResponse deposit(UUID userId, UUID goalId, DepositRequest request) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta nao encontrada: " + goalId));

        goal.setCurrentAmount(goal.getCurrentAmount().add(request.amount()));
        goal = goalRepository.save(goal);
        return GoalResponse.from(goal);
    }

    @Transactional
    public void delete(UUID userId, UUID goalId) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta nao encontrada: " + goalId));

        goalRepository.delete(goal);
    }
}

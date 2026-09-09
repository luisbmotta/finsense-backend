package com.finsense.backend.insights;

import com.finsense.backend.ai.GroqService;
import com.finsense.backend.common.exception.ResourceNotFoundException;
import com.finsense.backend.insights.dto.InsightsResponse;
import com.finsense.backend.transaction.Transaction;
import com.finsense.backend.transaction.TransactionRepository;
import com.finsense.backend.user.User;
import com.finsense.backend.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class InsightsService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final GroqService groqService;

    public InsightsService(
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            GroqService groqService
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.groqService = groqService;
    }

    @Transactional(readOnly = true)
    public InsightsResponse getInsights(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + userId));

        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDescCreatedAtDesc(userId);

        List<String> insights = groqService.gerarInsights(transactions, user.getMonthlyIncome());
        return new InsightsResponse(insights);
    }
}

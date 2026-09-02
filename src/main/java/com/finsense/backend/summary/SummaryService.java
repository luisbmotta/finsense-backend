package com.finsense.backend.summary;

import com.finsense.backend.common.exception.ResourceNotFoundException;
import com.finsense.backend.summary.dto.SummaryResponse;
import com.finsense.backend.transaction.Transaction;
import com.finsense.backend.transaction.TransactionRepository;
import com.finsense.backend.user.User;
import com.finsense.backend.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SummaryService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public SummaryService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public SummaryResponse getSummary(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + userId));

        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDescCreatedAtDesc(userId);

        BigDecimal totalExpenses = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> expensesByCategory = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getValue(),
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        BigDecimal monthlyIncome = user.getMonthlyIncome();
        BigDecimal balance = monthlyIncome.subtract(totalExpenses);

        return new SummaryResponse(monthlyIncome, totalExpenses, balance, expensesByCategory);
    }
}

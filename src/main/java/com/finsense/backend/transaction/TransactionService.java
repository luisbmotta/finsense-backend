package com.finsense.backend.transaction;

import com.finsense.backend.common.exception.ResourceNotFoundException;
import com.finsense.backend.transaction.dto.CreateTransactionRequest;
import com.finsense.backend.transaction.dto.TransactionResponse;
import com.finsense.backend.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> listForUser(UUID userId) {
        return transactionRepository.findByUserIdOrderByDateDescCreatedAtDesc(userId).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @Transactional
    public TransactionResponse create(User user, CreateTransactionRequest request) {
        Transaction transaction = new Transaction(
                user,
                request.description(),
                request.amount(),
                request.category(),
                request.date()
        );

        transaction = transactionRepository.save(transaction);
        return TransactionResponse.from(transaction);
    }

    @Transactional
    public void delete(UUID userId, UUID transactionId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transacao nao encontrada: " + transactionId));

        transactionRepository.delete(transaction);
    }
}

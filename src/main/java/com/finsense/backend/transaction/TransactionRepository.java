package com.finsense.backend.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUserIdOrderByDateDescCreatedAtDesc(UUID userId);

    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);
}

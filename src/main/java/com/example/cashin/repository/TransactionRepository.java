package com.example.cashin.repository;

import com.example.cashin.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomer_ExternalIdAndTimestampGreaterThanEqual(Long externalId, Instant timestamp);

    boolean existsByLoadIdAndCustomer_ExternalId(Long loadId, Long externalId);

}

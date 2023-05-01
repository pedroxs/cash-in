package com.example.cashin.service;

import com.example.cashin.model.Customer;
import com.example.cashin.model.Transaction;
import com.example.cashin.repository.CustomerRepository;
import com.example.cashin.repository.TransactionRepository;
import com.example.cashin.web.rest.vm.LoadRequestVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessTransactions {
    private static final Logger log = LoggerFactory.getLogger(ProcessTransactions.class);
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    public ProcessTransactions(TransactionRepository transactionRepository,
                               CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    public Transaction load(LoadRequestVM loadRequest) {
        Transaction transaction = loadRequest.toTransaction();
        boolean exists = transactionRepository.existsByLoadIdAndCustomer_ExternalId(transaction.getLoadId(), loadRequest.getCustomerId());
        if (exists) {
            return null;
        }

        Optional<Customer> optionalCustomer = customerRepository.findByExternalId(loadRequest.getCustomerId());
        if (optionalCustomer.isPresent()) {
            transaction.setCustomer(optionalCustomer.get());
        } else {
            transaction.setCustomer(createNewCustomer(loadRequest.getCustomerId()));
        }

        try {
            checkVelocity(transaction);
            transaction.setAccepted(true);
            updateCustomerBalance(transaction);
        } catch (Exception e) {
            log.info("Transaction not accepted", e);
            transaction.setAccepted(false);
        }

        return transactionRepository.save(transaction);
    }

    private void checkVelocity(Transaction transaction) {
        Instant endOfYesterday = transaction.getTimestamp().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);

        List<Transaction> transactions = transactionRepository.findByCustomer_ExternalIdAndTimestampGreaterThanEqual(
                transaction.getCustomer().getExternalId(),
                endOfYesterday.minus(7, ChronoUnit.DAYS)
        );

        transactions.add(transaction);

        long countTrxToday = transactions.stream()
                .filter(trx -> trx.getTimestamp().isAfter(endOfYesterday))
                .count();
        if (countTrxToday > 3) {
            //FIXME: avoid using throw pattern
            throw new RuntimeException("too many transactions");
        }

        BigDecimal amountTrxToday = transactions.stream()
                .filter(trx -> trx.getTimestamp().isAfter(endOfYesterday))
                .filter(trx -> null == trx.getAccepted() || trx.getAccepted())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (amountTrxToday.compareTo(new BigDecimal("5000")) > 0) {
            //FIXME: avoid using throw pattern
            throw new RuntimeException("reached capacity for the day");
        }

        BigDecimal amountTrxWeek = transactions.stream()
                .filter(trx -> trx.getTimestamp().isAfter(endOfYesterday.minus(6, ChronoUnit.DAYS)))
                .filter(trx -> null == trx.getAccepted() || trx.getAccepted())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (amountTrxWeek.compareTo(new BigDecimal("20000")) > 0) {
            //FIXME: avoid using throw pattern
            throw new RuntimeException("reached capacity for the week");
        }
    }

    private void updateCustomerBalance(Transaction transaction) {
        Customer customer = transaction.getCustomer();
        customerRepository.save(customer.updateAmount(transaction.getAmount()));
    }

    private Customer createNewCustomer(Long customerExternalId) {
        return customerRepository.save(new Customer(customerExternalId));
    }
}

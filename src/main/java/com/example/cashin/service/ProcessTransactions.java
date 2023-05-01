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
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessTransactions {

    private static final DayOfWeek firstDayOfWeek = WeekFields.ISO.getFirstDayOfWeek();

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


        Optional<String> velocity = checkVelocity(transaction);
        if (velocity.isPresent()) {
            log.info("Transaction not accepted: {}", velocity.get());
            transaction.setAccepted(false);
        } else {
            transaction.setAccepted(true);
            updateCustomerBalance(transaction);
        }

        return transactionRepository.save(transaction);
    }

    private Optional<String> checkVelocity(Transaction transaction) {
        Instant endOfYesterday = transaction.getTimestamp().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);
        Instant startOfWeek = transaction.getTimestamp()
                .truncatedTo(ChronoUnit.DAYS)
                .atOffset(ZoneOffset.UTC)
                .with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
                .minus(1, ChronoUnit.SECONDS)
                .toInstant();

        List<Transaction> transactions = transactionRepository.findByCustomer_ExternalIdAndTimestampGreaterThan(
                transaction.getCustomer().getExternalId(),
                startOfWeek
        );

        transactions.add(transaction);

        long countTrxToday = transactions.stream()
                .filter(trx -> trx.getTimestamp().isAfter(endOfYesterday))
                .count();
        if (countTrxToday > 3) {
            return Optional.of("too many transactions");
        }

        BigDecimal amountTrxToday = transactions.stream()
                .filter(trx -> trx.getTimestamp().isAfter(endOfYesterday))
                .filter(trx -> null == trx.getAccepted() || trx.getAccepted())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (amountTrxToday.compareTo(new BigDecimal("5000")) > 0) {
            return Optional.of("reached capacity for the day");
        }

        BigDecimal amountTrxWeek = transactions.stream()
                .filter(trx -> trx.getTimestamp().isAfter(startOfWeek))
                .filter(trx -> null == trx.getAccepted() || trx.getAccepted())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (amountTrxWeek.compareTo(new BigDecimal("20000")) > 0) {
            return Optional.of("reached capacity for the week");
        }

        return Optional.empty();
    }

    private void updateCustomerBalance(Transaction transaction) {
        Customer customer = transaction.getCustomer();
        customerRepository.save(customer.updateAmount(transaction.getAmount()));
    }

    private Customer createNewCustomer(Long customerExternalId) {
        return customerRepository.save(new Customer(customerExternalId));
    }
}

package com.example.cashin.service;

import com.example.cashin.model.Transaction;
import com.example.cashin.web.rest.vm.LoadRequestVM;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProcessTransactionsTest {

    @Autowired
    private ProcessTransactions processTransactions;

    @Test
    void singleTransactionLowerThenDayThresholdIsAccepted() {
        LoadRequestVM loadRequest = mockRequest();
        loadRequest.setCustomerId(1L);
        loadRequest.setLoadAmount("$500");
        Transaction transaction = processTransactions.load(loadRequest);
        assertTrue(transaction.getAccepted());
    }

    @Test
    void singleTransactionHigherThenDayThresholdIsRejected() {
        LoadRequestVM loadRequest = mockRequest();
        loadRequest.setCustomerId(2L);
        loadRequest.setLoadAmount("$5000.01");
        Transaction transaction = processTransactions.load(loadRequest);
        assertFalse(transaction.getAccepted());
    }

    @Test
    void fourthTransactionOnSameDayIsRejected() {
        LoadRequestVM loadRequest = mockRequest();
        loadRequest.setCustomerId(3L);

        Transaction transaction1 = processTransactions.load(loadRequest);
        assertTrue(transaction1.getAccepted());
        assertEquals(transaction1.getLoadId(), loadRequest.getId());

        loadRequest.setId(loadRequest.getId() + 1);
        Transaction transaction2 = processTransactions.load(loadRequest);
        assertTrue(transaction2.getAccepted());
        assertEquals(transaction2.getLoadId(), loadRequest.getId());

        loadRequest.setId(loadRequest.getId() + 1);
        Transaction transaction3 = processTransactions.load(loadRequest);
        assertTrue(transaction3.getAccepted());
        assertEquals(transaction3.getLoadId(), loadRequest.getId());

        loadRequest.setId(loadRequest.getId() + 1);
        Transaction transaction4 = processTransactions.load(loadRequest);
        assertFalse(transaction4.getAccepted());
        assertEquals(transaction4.getLoadId(), loadRequest.getId());
    }

    @Test
    void secondTransactionHigherThenDayThresholdIsRejected() {
        LoadRequestVM loadRequest = mockRequest();
        loadRequest.setCustomerId(4L);
        loadRequest.setLoadAmount("$3000");

        Transaction transaction1 = processTransactions.load(loadRequest);
        assertTrue(transaction1.getAccepted());
        assertEquals(transaction1.getLoadId(), loadRequest.getId());

        loadRequest.setId(loadRequest.getId() + 1);
        Transaction transaction2 = processTransactions.load(loadRequest);
        assertFalse(transaction2.getAccepted());
        assertEquals(transaction2.getLoadId(), loadRequest.getId());
    }

    @Test
    void transactionHigherThenWeekThresholdIsRejected() {
        LoadRequestVM loadRequest = mockRequest();
        loadRequest.setCustomerId(5L);
        loadRequest.setLoadAmount("$5000");

        Transaction transaction1 = processTransactions.load(loadRequest);
        assertTrue(transaction1.getAccepted());
        assertEquals(transaction1.getLoadId(), loadRequest.getId());

        loadRequest.setId(loadRequest.getId() + 1);
        loadRequest.setTime(loadRequest.getTime().plus(1, ChronoUnit.DAYS));
        Transaction transaction2 = processTransactions.load(loadRequest);
        assertTrue(transaction2.getAccepted());
        assertEquals(transaction2.getLoadId(), loadRequest.getId());

        loadRequest.setId(loadRequest.getId() + 1);
        loadRequest.setTime(loadRequest.getTime().plus(1, ChronoUnit.DAYS));
        Transaction transaction3 = processTransactions.load(loadRequest);
        assertTrue(transaction3.getAccepted());
        assertEquals(transaction3.getLoadId(), loadRequest.getId());

        loadRequest.setId(loadRequest.getId() + 1);
        loadRequest.setTime(loadRequest.getTime().plus(1, ChronoUnit.DAYS));
        Transaction transaction4 = processTransactions.load(loadRequest);
        assertTrue(transaction4.getAccepted());
        assertEquals(transaction4.getLoadId(), loadRequest.getId());

        loadRequest.setId(loadRequest.getId() + 1);
        loadRequest.setTime(loadRequest.getTime().plus(1, ChronoUnit.DAYS));
        Transaction transaction5 = processTransactions.load(loadRequest);
        assertFalse(transaction5.getAccepted());
        assertEquals(transaction5.getLoadId(), loadRequest.getId());
    }

    private LoadRequestVM mockRequest() {
        LoadRequestVM req = new LoadRequestVM();
        req.setId(1L);
        req.setLoadAmount("$500.01");
        req.setTime(Instant.parse("2023-05-01T10:15:30.00Z"));
        return req;
    }
}
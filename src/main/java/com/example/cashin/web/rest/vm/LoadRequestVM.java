package com.example.cashin.web.rest.vm;

import com.example.cashin.model.Transaction;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoadRequestVM {
    private Long id;
    private Long customerId;
    private String loadAmount;
    private Instant time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getLoadAmount() {
        return loadAmount;
    }

    public void setLoadAmount(String loadAmount) {
        this.loadAmount = loadAmount;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Transaction toTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(loadAmount.substring(1)));
        transaction.setTimestamp(time);
        transaction.setLoadId(id);
        transaction.setAccepted(true);
        return transaction;
    }

    @Override
    public String toString() {
        return "LoadRequestVM{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", loadAmount='" + loadAmount + '\'' +
                ", time=" + time +
                '}';
    }
}

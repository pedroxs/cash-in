package com.example.cashin.model;

import jakarta.persistence.*;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "transaction", uniqueConstraints = {
        @UniqueConstraint(name = "uc_transaction_load_id", columnNames = {"load_id", "customer_id"})
})
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
    @Column(name = "load_id", nullable = false)
    private Long loadId;
    @Column(name = "accepted", nullable = false)
    private Boolean accepted;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "user_username")
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Transient // DATAJPA-622
    public boolean isNew() {
        return null == getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Transaction that = (Transaction) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "amount = " + amount + ", " +
                "timestamp = " + timestamp + ", " +
                "loadId = " + loadId + ", " +
                "accepted = " + accepted + ", " +
                "user = " + user + ", " +
                "customer = " + customer + ")";
    }
}
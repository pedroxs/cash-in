package com.example.cashin.web.rest.vm;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoadResponseVM {
    private String id;
    private String customerId;
    private Boolean accepted;

    public LoadResponseVM() {
    }

    public LoadResponseVM(Long id, Long customerId, Boolean accepted) {
        this.id = id.toString();
        this.customerId = customerId.toString();
        this.accepted = accepted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public String toString() {
        return "LoadResponseVM{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", accepted=" + accepted +
                '}';
    }
}

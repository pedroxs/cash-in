package com.example.cashin.web.rest;

import com.example.cashin.model.Transaction;
import com.example.cashin.service.ProcessTransactions;
import com.example.cashin.web.rest.vm.LoadRequestVM;
import com.example.cashin.web.rest.vm.LoadResponseVM;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/load")
public class LoadResource {

    private final ProcessTransactions processTransactions;

    public LoadResource(ProcessTransactions processTransactions) {
        this.processTransactions = processTransactions;
    }

    @PostMapping
    public ResponseEntity<LoadResponseVM> loadCash(@RequestBody LoadRequestVM loadRequest) {
        Transaction transaction = processTransactions.load(loadRequest);
        if (transaction == null) {
            return ResponseEntity.noContent().build();
        }
        LoadResponseVM responseVM = new LoadResponseVM(transaction.getLoadId(), transaction.getCustomer().getExternalId(), transaction.getAccepted());
        return ResponseEntity.ok(responseVM);
    }
}

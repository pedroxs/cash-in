package com.example.cashin.web.rest;

import com.example.cashin.model.Customer;
import com.example.cashin.repository.CustomerRepository;
import com.example.cashin.web.rest.vm.PageVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerResource {
    private final CustomerRepository customerRepository;

    public CustomerResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public ResponseEntity<PageVM<Customer>> listCustomers(@PageableDefault Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return ResponseEntity.ok(new PageVM<>(customerPage));
    }
}

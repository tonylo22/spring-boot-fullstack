package com.example.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping(
            value = { "/customers", "/customers/" },
            method = GET)
    public List<Customer> getCustomers(@RequestParam(value = "age", required = false) Integer age) {
        return age == null ? customerService.getAllCustomers() : customerService.getCustomersByAge(age);
    }

    @PostMapping("/customers")
    public void addCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
    }

    @GetMapping("/customers/{customerId}")
    public Customer getCustomerById(@PathVariable("customerId") Integer customerId) {
        return customerService.getCustomerById(customerId);
    }

    @DeleteMapping("/customers/{customerId}")
    public void removeCustomerById(@PathVariable("customerId") Integer customerId) {
        customerService.removeCustomerById(customerId);
    }

    @PostMapping("/customers/{customerId}")
    public void updateCustomerById(@PathVariable("customerId") Integer customerId, @RequestBody CustomerUpdateRequest request) {
        customerService.updateCustomerById(customerId, request);
    }
}

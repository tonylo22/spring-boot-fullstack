package com.example.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    List<Customer> selectAllCustomers();
    List<Customer> selectCustomerByAge(Integer age);
    Optional<Customer> selectCustomerById(Integer id);
    void insertCustomer(Customer customer);
    void removeCustomerById(Integer id);
    void updateCustomer(Customer customer);
    boolean existsCustomerWithEmail(String email);
}

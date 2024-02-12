package com.example.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDAO{
    private static List<Customer> customers;

    static {
        customers = new ArrayList<>();
        customers.add(new Customer(1, "Tony", "tony@gmail.com", 30));
        customers.add(new Customer(2, "Ivy", "ivy@gmail.com", 34));
        customers.add(new Customer(3, "Loi", "loi@gmail.com", 34));
    }


    public CustomerListDataAccessService() {
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public List<Customer> selectCustomerByAge(Integer age) {
        return customers.stream().filter(c -> age.equals(c.getAge())).collect(Collectors.toList());
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customers.stream().filter(c -> id.equals(c.getId())).findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public void removeCustomerById(Integer id) {
        customers.removeIf(c -> c.getId().equals(id));
    }

    @Override
    public void updateCustomer(Customer customer) {
        selectCustomerById(customer.getId()).ifPresent(c -> mergeCustomer(customer, c));
    }

    private void mergeCustomer(Customer from, Customer to) {
        to.setName(from.getName());
        to.setEmail(from.getEmail());
        to.setAge(from.getAge());
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customers.stream().anyMatch(c -> c.getEmail().equals(email));
    }
}

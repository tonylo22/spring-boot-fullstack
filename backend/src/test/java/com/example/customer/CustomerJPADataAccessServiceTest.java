package com.example.customer;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        underTest.selectAllCustomers();
        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerByAge() {
        Integer age = 20;
        underTest.selectCustomerByAge(age);
        verify(customerRepository).findByAge(age);
    }

    @Test
    void selectCustomerById() {
        Integer id = 1;
        underTest.selectCustomerById(id);
        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        Faker FAKER = new Faker();
        String email = UUID.randomUUID() + "@" + FAKER.internet().domainName();
        String name = FAKER.name().fullName();
        Integer age = FAKER.number().numberBetween(20, 40);
        Customer customer = new Customer(name, email, age);

        underTest.insertCustomer(customer);
        verify(customerRepository).save(customer);
    }

    @Test
    void removeCustomerById() {
        Integer id = 1;
        underTest.removeCustomerById(id);
        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        Faker FAKER = new Faker();
        String email = UUID.randomUUID() + "@" + FAKER.internet().domainName();
        String name = FAKER.name().fullName();
        Integer age = FAKER.number().numberBetween(20, 40);
        Customer customer = new Customer(name, email, age);

        underTest.updateCustomer(customer);
        verify(customerRepository).save(customer);
    }

    @Test
    void existsCustomerWithEmail() {
        Faker FAKER = new Faker();
        String email = UUID.randomUUID() + "@" + FAKER.internet().domainName();
        underTest.existsCustomerWithEmail(email);
        verify(customerRepository).existsCustomerByEmail(email);
    }
}
package com.example.customer;

import com.example.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                UUID.randomUUID() + "@" + FAKER.internet().domainName(),
                20
        );

        underTest.insertCustomer(customer);
        List<Customer> expected = underTest.selectAllCustomers();
        assertThat(!expected.isEmpty());
    }

    @Test
    void selectCustomerByAge() {
        Integer targetAge = 45;

        Customer customer1 = new Customer(
                FAKER.name().fullName(),
                UUID.randomUUID() + "@" + FAKER.internet().domainName(),
                20
        );
        Customer customer2 = new Customer(
                FAKER.name().fullName(),
                UUID.randomUUID() + "@" + FAKER.internet().domainName(),
                targetAge
        );

        underTest.insertCustomer(customer1);
        underTest.insertCustomer(customer2);
        List<Customer> expected = underTest.selectCustomerByAge(targetAge);
        assertThat(expected.size() == 1);
        assertThat(expected.get(0).equals(customer2));
    }

    @Test
    void selectCustomerById() {
        String email = UUID.randomUUID() + "@" + FAKER.internet().domainName();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                33
        );

        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional actual = underTest.selectCustomerById(id);
        Consumer<Customer> consumer = c -> {
            assertThat(c.getId()).isEqualTo(id);
        };
        assertThat(actual).isPresent().hasValueSatisfying(consumer);
    }

    @Test
    void willReturnEmptyWhenSelectCustomerByNonExistentId() {
        Integer id = -1;
        Customer customer = new Customer(
                FAKER.name().fullName(),
                UUID.randomUUID() + "@" + FAKER.internet().domainName(),
                20
        );

        underTest.insertCustomer(customer);
        Optional actual = underTest.selectCustomerById(id);
        assertThat(actual.isEmpty());
    }

    @Test
    void insertCustomer() {
    }

    @Test
    void removeCustomerById() {
        String email = UUID.randomUUID() + "@" + FAKER.internet().domainName();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                33
        );

        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        underTest.removeCustomerById(id);
        assertThat(underTest.selectCustomerById(id)).isNotPresent();
    }

    @Test
    void updateCustomer() {
        String email = UUID.randomUUID() + "@" + FAKER.internet().domainName();
        String name = FAKER.name().fullName();
        Integer age = FAKER.number().numberBetween(20, 40);

        Customer customer = new Customer(name, email, age);
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer toBeUpdated;
        Customer updated;

        // update age
        Integer newAge = age + 1;
        toBeUpdated = new Customer(id, name, email, newAge);
        underTest.updateCustomer(toBeUpdated);
        updated = underTest.selectCustomerById(id).get();
        assertThat(updated.getAge().equals(newAge));
        assertThat(updated.getEmail().equals(email));
        assertThat(updated.getName().equals(name));

        // update email
        String newEmail = UUID.randomUUID() + "@" + FAKER.internet().domainName();
        toBeUpdated = new Customer(id, name, newEmail, newAge);
        underTest.updateCustomer(toBeUpdated);
        updated = underTest.selectCustomerById(id).get();
        assertThat(updated.getAge().equals(newAge));
        assertThat(updated.getEmail().equals(newEmail));
        assertThat(updated.getName().equals(name));

        // update name
        String newName = name + "2";
        toBeUpdated = new Customer(id, newName, newEmail, newAge);
        underTest.updateCustomer(toBeUpdated);
        updated = underTest.selectCustomerById(id).get();
        assertThat(updated.getAge().equals(newAge));
        assertThat(updated.getEmail().equals(newEmail));
        assertThat(updated.getName().equals(newName));
    }

    @Test
    void existsCustomerWithEmail() {
        String email = UUID.randomUUID() + "@" + FAKER.internet().domainName();
        String name = FAKER.name().fullName();
        Integer age = FAKER.number().numberBetween(20, 40);

        Customer customer = new Customer(name, email, age);
        underTest.insertCustomer(customer);

        assertThat(underTest.existsCustomerWithEmail(email)).isTrue();
    }
}
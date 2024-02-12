package com.example.customer;

import com.example.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CustomerRepository underTest;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    void findByAge() {
        String email = UUID.randomUUID() + "@" + FAKER.internet().domainName();
        String name = FAKER.name().fullName();
        Integer age = FAKER.number().numberBetween(20, 40);

        Customer customer = new Customer(name, email, age);
        underTest.save(customer);

        List<Customer> actual = underTest.findByAge(age);
        assertThat(actual.stream().map(c -> c.getAge()).findFirst()).isPresent().get().isEqualTo(age);
    }

    @Test
    void findByNonExistentAge() {
        String email = UUID.randomUUID() + "@" + FAKER.internet().domainName();
        String name = FAKER.name().fullName();
        Integer age = 0;

        Customer customer = new Customer(name, email, age);
        underTest.save(customer);

        List<Customer> actual = underTest.findByAge(age);
        assertThat(actual.isEmpty());
    }

    @Test
    void existsCustomerByEmail() {
        String email = UUID.randomUUID() + "@" + FAKER.internet().domainName();
        String name = FAKER.name().fullName();
        Integer age = FAKER.number().numberBetween(20, 40);

        Customer customer = new Customer(name, email, age);
        underTest.save(customer);

        boolean actual = underTest.existsCustomerByEmail(email);
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByNonExistentEmail() {
        String email = UUID.randomUUID() + "@" + FAKER.internet().domainName();
        String name = FAKER.name().fullName();
        Integer age = FAKER.number().numberBetween(20, 40);

        Customer customer = new Customer(name, email, age);
        underTest.save(customer);

        boolean actual = underTest.existsCustomerByEmail(UUID.randomUUID() + "@" + FAKER.internet().domainName());
        assertThat(actual).isFalse();
    }
}
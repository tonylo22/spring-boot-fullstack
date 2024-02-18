package com.example;

import com.example.customer.Customer;
import com.example.customer.CustomerRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            Faker faker = new Faker();
            List<Customer> customers = new ArrayList<>();
            customers.add(new Customer(faker.name().firstName(), faker.internet().emailAddress(), faker.number().numberBetween(16, 65)));
            customers.add(new Customer(faker.name().firstName(), faker.internet().emailAddress(), faker.number().numberBetween(16, 65)));
            customerRepository.saveAll(customers);
        };
    }
}

package com.example.journey;

import com.example.customer.Customer;
import com.example.customer.CustomerRegistrationRequest;
import com.example.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void canRegisterCustomer() {
        Faker faker = new Faker();
        String baseUrl = "/customers";
        // create a registration request
        String name = faker.name().fullName();
        String email = UUID.randomUUID().toString() + '@' + faker.internet().domainName();
        Integer age = faker.number().numberBetween(20, 70);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age);

        // send POST request to api
        webTestClient.post().uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get().uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure customer is present
        Customer expectedCustomer = new Customer(name, email, age);
        assertThat(allCustomers).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        // get customer by id
        var id = allCustomers.stream().filter(c -> c.getEmail().equals(email)).map(Customer::getId).findFirst().orElseThrow();
        expectedCustomer.setId(id);

        webTestClient.get().uri(baseUrl + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        // insert a customer
        Faker faker = new Faker();
        String baseUrl = "/customers";
        // create a registration request
        String name = faker.name().fullName();
        String email = UUID.randomUUID().toString() + '@' + faker.internet().domainName();
        Integer age = faker.number().numberBetween(20, 70);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age);

        // send POST request to api
        webTestClient.post().uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get().uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // get customer id
        Customer expectedCustomer = new Customer(name, email, age);
        var id = allCustomers.stream().filter(c -> c.getEmail().equals(email)).map(Customer::getId).findFirst().orElseThrow();

        //try to delete
        webTestClient.delete().uri(baseUrl + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        // verify by getting by id
        webTestClient.get().uri(baseUrl + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // insert a customer
        Faker faker = new Faker();
        String baseUrl = "/customers";
        // create a registration request
        String name = faker.name().fullName();
        String email = UUID.randomUUID().toString() + '@' + faker.internet().domainName();
        Integer age = faker.number().numberBetween(20, 70);
        CustomerRegistrationRequest registerRequest = new CustomerRegistrationRequest(name, email, age);

        // send POST request to api
        webTestClient.post().uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registerRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get().uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // get customer id
        Customer expectedCustomer = new Customer(name, email, age);
        var id = allCustomers.stream().filter(c -> c.getEmail().equals(email)).map(Customer::getId).findFirst().orElseThrow();

        // create update request
        String newName = name + "2";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(newName, email, age);
        Customer expectedUpdatedCustomer = new Customer(id, newName, email, age);

        // send POST request to update
        webTestClient.post().uri(baseUrl + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // verify updated
        webTestClient.get().uri(baseUrl + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedUpdatedCustomer);

    }
}

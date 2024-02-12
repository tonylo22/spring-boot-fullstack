package com.example.customer;

import com.example.exception.CreateCustomerInvalidatedException;
import com.example.exception.EmailTakenException;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.UpdateCustomerInvalidatedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;
    @Mock CustomerDAO customerDAO;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO);
    }

    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();
        verify(customerDAO).selectAllCustomers();
    }

    @Test
    void getCustomersByAge() {
        Integer age = 20;
        underTest.getCustomersByAge(age);
        verify(customerDAO).selectCustomerByAge(age);
    }

    @Test
    void canGetCustomerById() {
        Integer id = 1;
        Customer customer = new Customer(id, "foo", "foo@bar.com", 30);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        Customer actual = underTest.getCustomerById(id);
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void getZeroCustomerById() {
        Integer id = 10;
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer with id [%s] is not found".formatted(id));
    }

    @Test
    void addCustomer() {
        String email = "foo@bar.com";
        Integer age = 30;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("foo", email, age);
        when(customerDAO.existsCustomerWithEmail(email)).thenReturn(false);

        underTest.addCustomer(request);
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).insertCustomer(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(request.name());
        assertThat(captor.getValue().getEmail()).isEqualTo(request.email());
        assertThat(captor.getValue().getAge()).isEqualTo(request.age());
    }

    @Test
    void addCustomerWithDuplicateEmail() {
        String email = "foo@bar.com";
        Integer age = 30;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("foo", email, age);
        when(customerDAO.existsCustomerWithEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(EmailTakenException.class)
                .hasMessageContaining("Email already taken");

        verify(customerDAO, never()).insertCustomer(any());
    }

    @Test
    void addCustomerWithInvalidEmail() {
        String email = "bar";
        Integer age = 30;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("foo", email, age);

        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(CreateCustomerInvalidatedException.class)
                .hasMessageContaining("The age or email is invalid");

        verify(customerDAO, never()).insertCustomer(any());
    }

    @Test
    void addCustomerWithInvalidAge() {
        String email = "foo@bar.com";
        Integer age = 1;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("foo", email, age);

        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(CreateCustomerInvalidatedException.class)
                .hasMessageContaining("The age or email is invalid");

        verify(customerDAO, never()).insertCustomer(any());
    }

    @Test
    void canRemoveCustomerById() {
        Integer id = 1;
        Customer customer = new Customer(id, "foo", "foo@bar.com", 30);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        underTest.removeCustomerById(id);
        verify(customerDAO).removeCustomerById(id);
    }

    @Test
    void willThrowWhenRemoveCustomerByNonExistentId() {
        Integer id = -1;
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.removeCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer with the requested id is not found");
        verify(customerDAO, never()).removeCustomerById(any());
    }

    @Test
    void updateCustomerNameById() {
        Integer id = 1;
        String originalName = "foo";
        String originalEmail = "foo@bar.com";
        Integer originalAge = 30;
        Customer customer = new Customer(id, originalName, originalEmail, originalAge);
        CustomerUpdateRequest request = new CustomerUpdateRequest("bar", null, null);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        underTest.updateCustomerById(id, request);
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(request.name());
        assertThat(captor.getValue().getEmail()).isEqualTo(originalEmail);
        assertThat(captor.getValue().getAge()).isEqualTo(originalAge);
    }

    @Test
    void updateCustomerEmailById() {
        Integer id = 1;
        String originalName = "foo";
        String originalEmail = "foo@bar.com";
        Integer originalAge = 30;
        Customer customer = new Customer(id, originalName, originalEmail, originalAge);
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, "bar@bar.com", null);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDAO.existsCustomerWithEmail(request.email())).thenReturn(false);

        underTest.updateCustomerById(id, request);
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(originalName);
        assertThat(captor.getValue().getEmail()).isEqualTo(request.email());
        assertThat(captor.getValue().getAge()).isEqualTo(originalAge);
    }

    @Test
    void updateCustomerAgeById() {
        Integer id = 1;
        String originalName = "foo";
        String originalEmail = "foo@bar.com";
        Integer originalAge = 30;
        Customer customer = new Customer(id, originalName, originalEmail, originalAge);
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, 40);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        underTest.updateCustomerById(id, request);
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(originalName);
        assertThat(captor.getValue().getEmail()).isEqualTo(originalEmail);
        assertThat(captor.getValue().getAge()).isEqualTo(request.age());
    }

    @Test
    void doNotUpdateCustomerWhenNoNewData() {
        Integer id = 1;
        String originalName = "foo";
        String originalEmail = "foo@bar.com";
        Integer originalAge = 30;
        Customer customer = new Customer(id, originalName, originalEmail, originalAge);
        CustomerUpdateRequest request = new CustomerUpdateRequest(originalName, originalEmail, originalAge);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> underTest.updateCustomerById(id, request))
                .isInstanceOf(UpdateCustomerInvalidatedException.class)
                .hasMessageContaining("No valid, new data is received, no update is made");
        verify(customerDAO, never()).removeCustomerById(any());
    }

    @Test
    void doNotUpdateCustomerWhenEmailIsTaken() {
        Integer id = 1;
        String originalName = "foo";
        String originalEmail = "foo@bar.com";
        Integer originalAge = 30;
        Customer customer = new Customer(id, originalName, originalEmail, originalAge);
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, "bar@bar.com", null);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDAO.existsCustomerWithEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> underTest.updateCustomerById(id, request))
                .isInstanceOf(EmailTakenException.class)
                .hasMessageContaining("Email already taken");
        verify(customerDAO, never()).removeCustomerById(any());
    }
}
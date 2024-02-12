package com.example.customer;

import com.example.exception.CreateCustomerInvalidatedException;
import com.example.exception.EmailTakenException;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.UpdateCustomerInvalidatedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.*;

@Service
public class CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.selectAllCustomers();
    }

    public List<Customer> getCustomersByAge(Integer age) {
        return customerDAO.selectCustomerByAge(age);
    }

    public Customer getCustomerById(Integer id) {
        return customerDAO.selectCustomerById(id).
                orElseThrow(() -> new ResourceNotFoundException("Customer with id [%s] is not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest request) {
        if (!validateEmail(request.email()) || !validateAge(request.age())) {
            throw new CreateCustomerInvalidatedException("The age or email is invalid");
        } else if (customerDAO.existsCustomerWithEmail(request.email())) {
            throw new EmailTakenException("Email already taken");
        } else {
            Customer customer = new Customer(
                    request.name(), request.email(), request.age()
            );
            customerDAO.insertCustomer(customer);
        }
    }

    public void removeCustomerById(Integer id) {
        customerDAO.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with the requested id is not found"));
        customerDAO.removeCustomerById(id);
    }

    public void updateCustomerById(Integer id, CustomerUpdateRequest request) {
        Customer targetCustomer = getCustomerById(id);
        boolean hasUpdated = false;
        if (request.name() != null && !request.name().isBlank() && !request.name().equals(targetCustomer.getName())) {
            targetCustomer.setName(request.name());
            hasUpdated = true;
        }
        if (request.email() != null && !request.email().isBlank() && !request.email().equals(targetCustomer.getEmail())) {
            if (customerDAO.existsCustomerWithEmail(request.email())) {
                throw new EmailTakenException("Email already taken");
            }
            targetCustomer.setEmail(request.email());
            hasUpdated = true;
        }
        if (request.age() != null && !request.age().equals(targetCustomer.getAge())) {
            targetCustomer.setAge(request.age());
            hasUpdated = true;
        }
        if (!hasUpdated) {
            throw new UpdateCustomerInvalidatedException("No valid, new data is received, no update is made");
        }
        customerDAO.updateCustomer(targetCustomer);

    }

    private boolean validateEmail(String emailStr) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }

    private boolean validateAge(Integer age) {
        return age >= 16;
    }
}

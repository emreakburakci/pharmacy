package com.example.application.data.service;

import com.example.application.data.entity.Customer;
import com.example.application.data.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;


    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;

    }

    public static String formatPhoneNumber(String phone) {

        String code = phone.substring(0,3);
        String remaining = phone.substring(3);

        return "(" + code + ")" + remaining;


    }

    public List<Customer> findAllCustomer() {

            return customerRepository.findAll();

    }

    
    public long countCustomer() {
        return customerRepository.count();
    }

    public void deleteCustomer(Customer customer) {
        customerRepository.delete(customer);
    }

    public void saveCustomer(Customer customer) {
        if (customer == null) {
            System.err.println("Customer is null. Are you sure you have connected your form to the application?");
            return;
        }
        customerRepository.save(customer);
    }

    public Customer findById(Long id) {
       return customerRepository.findById(id).get();
    }

    public Customer saveAndFlush(Customer customer) {
        return customerRepository.saveAndFlush(customer);
    }

}

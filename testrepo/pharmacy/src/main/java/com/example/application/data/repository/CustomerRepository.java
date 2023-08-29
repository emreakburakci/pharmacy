package com.example.application.data.repository;

import com.example.application.data.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select p from Customer p " +
        "where lower(p.name) like lower(concat('%', :searchTerm, '%')) " +
        "or lower(p.lastName) like lower(concat('%', :searchTerm, '%'))")
    List<Customer> search(@Param("searchTerm") String searchTerm);
}

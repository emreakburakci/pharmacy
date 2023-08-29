package com.example.application.data.service;

import com.example.application.data.entity.Customer;
import com.example.application.data.entity.Product;
import com.example.application.data.repository.CustomerRepository;
import com.example.application.data.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;

    }

    public List<Product> findAllProduct() {
            return productRepository.findAll();
    }

    
    public long countProduct() {
        return productRepository.count();
    }

    public void deleteCustomer(Product product) {
        productRepository.delete(product);
    }

    public void saveProduct(Product product) {
        if (product == null) {
            System.err.println("Product is null. Are you sure you have connected your form to the application?");
            return;
        }
        productRepository.save(product);
    }

    public Product findById(Long id) {
       return productRepository.findById(id).get();
    }

    public Product saveAndFlush(Product product) {
        return productRepository.saveAndFlush(product);
    }

}

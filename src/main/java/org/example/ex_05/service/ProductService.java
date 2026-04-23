package org.example.ex_05.service;

import org.example.ex_05.model.Product;
import org.example.ex_05.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> listProducts() {
        return productRepository.findAllWithVendor();
    }
}


package com.example.estore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.estore.dto.ProductRequest;
import com.example.estore.enums.Category;
import com.example.estore.model.Product;
import com.example.estore.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

   
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    
    public Product add(ProductRequest request) {
        Product product = mapToEntity(request);
        return productRepository.save(product);
    }

   
    public Product update(Long id, ProductRequest request) {

        Product product = getById(id);

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImage(request.getImage());
        product.setCategory(request.getCategory());
        product.setStock(request.getStock());

        return productRepository.save(product);
    }

    
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(id);
    }

    
    public List<Product> getByCategory(String category) {
        try {
            Category cat = Category.valueOf(category.toUpperCase());
            return productRepository.findByCategory(cat);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByTitleContainingIgnoreCase(query);
    }

    
    private Product mapToEntity(ProductRequest request) {
        Product product = new Product();

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImage(request.getImage());
        product.setCategory(request.getCategory());
        product.setStock(request.getStock());

        return product;
    }
}
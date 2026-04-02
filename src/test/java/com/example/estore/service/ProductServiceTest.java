package com.example.estore.service;

import com.example.estore.enums.Category;
import com.example.estore.model.Product;
import com.example.estore.repository.ProductRepository;
import com.example.estore.dto.ProductRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    // ---------------- TEST GET ALL ----------------
    @Test
    void testGetAll() {
        Product p = new Product();
        p.setTitle("Laptop");

        Mockito.when(productRepository.findAll()).thenReturn(List.of(p));

        List<Product> result = productService.getAll();

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getTitle());
    }

    // ---------------- TEST GET BY ID ----------------
    @Test
    void testGetById() {
        Product p = new Product();
        p.setId(1L);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        Product result = productService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void testGetById_NotFound() {
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getById(1L));
    }

    // ---------------- TEST ADD PRODUCT ----------------
    @Test
    void testAddProduct() {
        ProductRequest req = new ProductRequest();
        req.setTitle("Phone");
        req.setDescription("Smartphone");
        req.setPrice(1500.0);
        req.setCategory(Category.ELECTRONICS);
        req.setStock(10);
        req.setImage("phone.jpg");

        Product savedProduct = new Product();
        savedProduct.setTitle(req.getTitle());

        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(savedProduct);

        Product result = productService.add(req);

        assertEquals("Phone", result.getTitle());
    }

    // ---------------- TEST UPDATE PRODUCT ----------------
    @Test
    void testUpdateProduct() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setTitle("Old Title");

        ProductRequest updated = new ProductRequest();
        updated.setTitle("Updated");
        updated.setDescription("Updated Description");
        updated.setPrice(2000.0);
        updated.setCategory(Category.ELECTRONICS);
        updated.setStock(5);
        updated.setImage("updated.jpg");

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(productRepository.save(existing)).thenReturn(existing);

        Product result = productService.update(1L, updated);

        assertEquals("Updated", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(2000.0, result.getPrice());
    }

    // ---------------- TEST DELETE PRODUCT ----------------
    @Test
    void testDelete() {
        productService.delete(1L);
        Mockito.verify(productRepository).deleteById(1L);
    }

    // ---------------- TEST GET BY CATEGORY ----------------
    @Test
    void testGetByCategory() {
        Product p = new Product();
        p.setCategory(Category.ELECTRONICS);

        Mockito.when(productRepository.findByCategory(Category.ELECTRONICS))
                .thenReturn(List.of(p));

        List<Product> result = productService.getByCategory("electronics");

        assertEquals(1, result.size());
    }

    @Test
    void testInvalidCategory() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.getByCategory("wrong"));
    }

    // ---------------- TEST SEARCH PRODUCTS ----------------
    @Test
    void testSearchProducts() {
        Product p = new Product();
        p.setTitle("Laptop");

        Mockito.when(productRepository.findByTitleContainingIgnoreCase("lap"))
                .thenReturn(List.of(p));

        List<Product> result = productService.searchProducts("lap");

        assertEquals(1, result.size());
    }
}
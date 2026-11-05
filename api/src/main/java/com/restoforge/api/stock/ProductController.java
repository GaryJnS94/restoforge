package com.restoforge.api.stock;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restoforge.api.stock.dto.ProductResponse;

@RestController

@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // injection par constructeur, comme pour le service (RES-25)
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        // 1. demander les produits au service
        List<Product> products = productService.getAllProducts();
        // 2. convertir chaque Product en ProductResponse
        return products.stream()
            .map(ProductResponse::from)
            .toList();
    }
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
    return ProductResponse.from(productService.getProductById(id));
}
}

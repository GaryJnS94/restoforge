package com.restoforge.api.stock.product;

import com.restoforge.api.stock.dto.ProductResponse;
import com.restoforge.api.stock.dto.ProductUpsertRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public List<ProductResponse> getAllProducts(
      @RequestParam(required = false) Boolean belowThreshold) {
    boolean filter = Boolean.TRUE.equals(belowThreshold);
    List<Product> products = productService.getAllProducts(filter);
    return products.stream()
        .map(ProductResponse::from)
        .toList();
  }

  @GetMapping("/{id}")
  public ProductResponse getProductById(@PathVariable Long id) {
    return ProductResponse.from(productService.getProductById(id));
  }

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(
      @Valid @RequestBody ProductUpsertRequest request) {
    Product created = productService.createProduct(request);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(created.getId())
        .toUri();
    return ResponseEntity.created(location).body(ProductResponse.from(created));
  }

  @PutMapping("/{id}")
  public ProductResponse updateProduct(
      @PathVariable Long id, @Valid @RequestBody ProductUpsertRequest request) {
    return ProductResponse.from(productService.updateProduct(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}
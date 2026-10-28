package com.restoforge.api.stock;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logique métier des produits. Lecture seule pour l'instant : la création et
 * la modification arriveront avec le CRUD.
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

  private final ProductRepository productRepository;

  /** Injection par constructeur : instanciable sans Spring dans les tests. */
  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  public Product getProductById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }
}

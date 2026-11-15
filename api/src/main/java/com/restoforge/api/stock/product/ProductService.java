package com.restoforge.api.stock.product;

import com.restoforge.api.stock.dto.ProductUpsertRequest;
import com.restoforge.api.stock.supplier.Supplier;
import com.restoforge.api.stock.supplier.SupplierNotFoundException;
import com.restoforge.api.stock.supplier.SupplierRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

  private final ProductRepository productRepository;
  private final SupplierRepository supplierRepository;

  public ProductService(ProductRepository productRepository, SupplierRepository supplierRepository) {
    this.productRepository = productRepository;
    this.supplierRepository = supplierRepository;
  }

  public List<Product> getAllProducts(boolean belowThresholdOnly) {
    if (belowThresholdOnly) {
      return productRepository.findAllBelowThreshold();
    }
    return productRepository.findAll();
  }

  public Product getProductById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }

  @Transactional
  public Product createProduct(ProductUpsertRequest request) {
    Supplier supplier = supplierRepository.findById(request.supplierId())
        .orElseThrow(() -> new SupplierNotFoundException(request.supplierId()));
    Product product = new Product(
        request.name(),
        request.unit(),
        request.unitPrice(),
        supplier,
        request.alertThreshold());
    product.replaceStock(request.stock());
    return productRepository.save(product);
  }

  @Transactional
  public Product updateProduct(Long id, ProductUpsertRequest request) {
    Product product = getProductById(id);
    Supplier supplier = supplierRepository.findById(request.supplierId())
        .orElseThrow(() -> new SupplierNotFoundException(request.supplierId()));
    product.setName(request.name());
    product.setUnit(request.unit());
    product.setUnitPrice(request.unitPrice());
    product.setSupplier(supplier);
    product.setAlertThreshold(request.alertThreshold());
    product.replaceStock(request.stock());
    return productRepository.save(product);
  }

  @Transactional
  public void deleteProduct(Long id) {
    Product product = getProductById(id);
    if (productRepository.existsOrderLineForProduct(id)) {
      throw new ProductReferencedException(id);
    }
    productRepository.delete(product);
  }
}
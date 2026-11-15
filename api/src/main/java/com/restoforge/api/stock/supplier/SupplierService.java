package com.restoforge.api.stock.supplier;

import com.restoforge.api.stock.dto.SupplierUpsertRequest;
import com.restoforge.api.stock.product.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SupplierService {

  private final SupplierRepository supplierRepository;
  private final ProductRepository productRepository;

  public SupplierService(
      SupplierRepository supplierRepository, ProductRepository productRepository) {
    this.supplierRepository = supplierRepository;
    this.productRepository = productRepository;
  }

  public List<Supplier> getAllSuppliers() {
    return supplierRepository.findAll();
  }

  public Supplier getSupplierById(Long id) {
    return supplierRepository.findById(id)
        .orElseThrow(() -> new SupplierNotFoundException(id));
  }

  @Transactional
  public Supplier createSupplier(SupplierUpsertRequest request) {
    Supplier supplier = new Supplier(request.name());
    applyOptionalFields(supplier, request);
    return supplierRepository.save(supplier);
  }

  @Transactional
  public Supplier updateSupplier(Long id, SupplierUpsertRequest request) {
    Supplier supplier = getSupplierById(id);
    supplier.setName(request.name());
    applyOptionalFields(supplier, request);
    return supplierRepository.save(supplier);
  }

  @Transactional
  public void deleteSupplier(Long id) {
    Supplier supplier = getSupplierById(id);
    if (productRepository.existsBySupplierId(id) || supplierRepository.existsOrderForSupplier(id)) {
      throw new SupplierReferencedException(id);
    }
    supplierRepository.delete(supplier);
  }

  private static void applyOptionalFields(Supplier supplier, SupplierUpsertRequest request) {
    supplier.setEmail(request.email());
    supplier.setPhone(request.phone());
    supplier.setAddress(request.address());
  }
}
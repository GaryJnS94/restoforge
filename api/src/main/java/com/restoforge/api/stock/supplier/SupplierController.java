package com.restoforge.api.stock.supplier;

import com.restoforge.api.stock.dto.SupplierResponse;
import com.restoforge.api.stock.dto.SupplierUpsertRequest;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

  private final SupplierService supplierService;

  public SupplierController(SupplierService supplierService) {
    this.supplierService = supplierService;
  }

  @GetMapping
  public List<SupplierResponse> getAllSuppliers() {
    return supplierService.getAllSuppliers().stream()
        .map(SupplierResponse::from)
        .toList();
  }

  @GetMapping("/{id}")
  public SupplierResponse getSupplierById(@PathVariable Long id) {
    return SupplierResponse.from(supplierService.getSupplierById(id));
  }

  @PostMapping
  public ResponseEntity<SupplierResponse> createSupplier(
      @Valid @RequestBody SupplierUpsertRequest request) {
    Supplier created = supplierService.createSupplier(request);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(created.getId())
        .toUri();
    return ResponseEntity.created(location).body(SupplierResponse.from(created));
  }

  @PutMapping("/{id}")
  public SupplierResponse updateSupplier(
      @PathVariable Long id, @Valid @RequestBody SupplierUpsertRequest request) {
    return SupplierResponse.from(supplierService.updateSupplier(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
    supplierService.deleteSupplier(id);
    return ResponseEntity.noContent().build();
  }
}
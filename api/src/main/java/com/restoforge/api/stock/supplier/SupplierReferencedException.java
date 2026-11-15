package com.restoforge.api.stock.supplier;

public class SupplierReferencedException extends RuntimeException {

  public SupplierReferencedException(Long id) {
    super("Supplier cannot be deleted while products or orders reference id: " + id);
  }
}
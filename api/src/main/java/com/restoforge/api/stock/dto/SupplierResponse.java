package com.restoforge.api.stock.dto;

import com.restoforge.api.stock.supplier.Supplier;

public record SupplierResponse(
    Long id,
    String name,
    String email,
    String phone,
    String address
) {

  public static SupplierResponse from(Supplier supplier) {
    return new SupplierResponse(
        supplier.getId(),
        supplier.getName(),
        supplier.getEmail(),
        supplier.getPhone(),
        supplier.getAddress());
  }
}
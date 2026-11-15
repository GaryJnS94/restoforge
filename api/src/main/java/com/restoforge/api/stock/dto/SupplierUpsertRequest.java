package com.restoforge.api.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierUpsertRequest(
    @NotBlank @Size(max = 150) String name,
  @Size(max = 255) String email,
  @Size(max = 30) String phone,
  String address
) {
}
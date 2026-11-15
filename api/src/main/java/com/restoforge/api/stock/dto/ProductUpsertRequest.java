package com.restoforge.api.stock.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductUpsertRequest(
    @NotBlank @Size(max = 150) String name,
    @NotBlank @Size(max = 20) String unit,
    @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal unitPrice,
    @NotNull Long supplierId,
    @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal alertThreshold,
    @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal stock
) {
}
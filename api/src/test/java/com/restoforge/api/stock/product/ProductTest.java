package com.restoforge.api.stock.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.restoforge.api.stock.supplier.Supplier;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Règle 14 — indicateur « sous le seuil ». */
class ProductTest {

  @Test
  @DisplayName("stock strictement inférieur au seuil → belowThreshold")
  void isBelowThreshold_quandStockInferieurAuSeuil() {
    Product product = new Product(
        "Farine", "kg", new BigDecimal("1"), new Supplier("Metro"), new BigDecimal("10"));
    product.replaceStock(new BigDecimal("9.999"));

    assertThat(product.isBelowThreshold()).isTrue();
  }

  @Test
  @DisplayName("stock égal au seuil → pas belowThreshold")
  void isBelowThreshold_quandStockEgalAuSeuil() {
    Product product = new Product(
        "Farine", "kg", new BigDecimal("1"), new Supplier("Metro"), new BigDecimal("10"));
    product.replaceStock(new BigDecimal("10"));

    assertThat(product.isBelowThreshold()).isFalse();
  }
}
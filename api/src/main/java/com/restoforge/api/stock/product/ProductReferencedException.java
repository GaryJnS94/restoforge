package com.restoforge.api.stock.product;

public class ProductReferencedException extends RuntimeException {

  public ProductReferencedException(Long id) {
    super("Product cannot be deleted while order lines reference id: " + id);
  }
}
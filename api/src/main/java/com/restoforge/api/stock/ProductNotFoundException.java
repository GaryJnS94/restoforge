package com.restoforge.api.stock;

/**
 * Exception métier : aucun produit pour cet id. Elle ne porte aucun code HTTP,
 * car il dépend du contexte d'appel (404 sur /products/{id}, 422 pour un
 * productId dans un corps de requête). Le web fait la traduction (RES-26).
 */
public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException(Long id) {
    super("Product not found with id: " + id);
  }
}

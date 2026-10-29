package com.restoforge.api.stock.dto;

import java.math.BigDecimal;

import com.restoforge.api.stock.Product;

/**
 * Représentation JSON d'un produit renvoyée par l'API.
 * L'entité Product ne sort jamais du controller : c'est ce record qui est sérialisé.
 */
public record ProductResponse(
        Long id,
        String name,
        String unit,
        BigDecimal unitPrice,
        BigDecimal stock,
        BigDecimal alertThreshold,
        boolean belowThreshold,   // calculé par l'entité, jamais null : le primitif est acceptable
        Long supplierId,
        String supplierName
) {

    /**
     * Convertit l'entité en réponse HTTP.
     * Lit le fournisseur (relation LAZY) : une fois OSIV désactivé,
     * le service doit l'avoir chargé, sinon LazyInitializationException.
     */
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getUnit(),
                product.getUnitPrice(),
                product.getStock(),
                product.getAlertThreshold(),
                product.isBelowThreshold(),
                product.getSupplier().getId(),    // on traverse la relation Product → Supplier
                product.getSupplier().getName()
        );
    }
}
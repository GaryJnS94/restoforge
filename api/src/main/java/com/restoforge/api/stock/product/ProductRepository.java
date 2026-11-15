package com.restoforge.api.stock.product;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Accès aux données des produits.
 * Spring Data génère l'implémentation au démarrage : findAll, findById,
 * save, deleteById, count… sont hérités de JpaRepository.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

  boolean existsBySupplierId(Long supplierId);

  @Query(
      value = "SELECT EXISTS (SELECT 1 FROM supplier_order_line WHERE product_id = :productId)",
      nativeQuery = true)
  boolean existsOrderLineForProduct(@Param("productId") Long productId);

  /**
   * Charge le fournisseur dans la même requête : ProductResponse lit
   * supplier hors session (open-in-view: false).
   */
  @Override
  @EntityGraph(attributePaths = "supplier")
  List<Product> findAll();

  @Override
  @EntityGraph(attributePaths = "supplier")
  Optional<Product> findById(Long id);

  /**
   * Règle 14 : strictement inférieur au seuil (pas d'égalité).
   */
  @EntityGraph(attributePaths = "supplier")
  @Query("SELECT p FROM Product p WHERE p.stock < p.alertThreshold")
  List<Product> findAllBelowThreshold();
}
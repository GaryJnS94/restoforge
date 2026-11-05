package com.restoforge.api.stock;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Accès aux données des produits.
 * Spring Data génère l'implémentation au démarrage : findAll, findById,
 * save, deleteById, count… sont hérités de JpaRepository.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

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
}
package com.restoforge.api.stock;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Accès aux données des produits.
 * Spring Data génère l'implémentation au démarrage : findAll, findById,
 * save, deleteById, count… sont hérités de JpaRepository.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
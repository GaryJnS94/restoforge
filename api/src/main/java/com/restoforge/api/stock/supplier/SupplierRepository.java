package com.restoforge.api.stock.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

  @Query(
      value = "SELECT EXISTS (SELECT 1 FROM supplier_order WHERE supplier_id = :supplierId)",
      nativeQuery = true)
  boolean existsOrderForSupplier(@Param("supplierId") Long supplierId);
}
package com.restoforge.api.stock.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.restoforge.api.stock.dto.SupplierUpsertRequest;
import com.restoforge.api.stock.product.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

  @Mock
  private SupplierRepository supplierRepository;

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private SupplierService supplierService;

  @Test
  @DisplayName("règle 13 — create avec le seul nom")
  void createSupplier_nomSeul() {
    when(supplierRepository.save(any(Supplier.class))).thenAnswer(invocation -> {
      Supplier saved = invocation.getArgument(0);
      saved.getClass(); // entité sans id tant que non persistée
      return saved;
    });

    Supplier created = supplierService.createSupplier(new SupplierUpsertRequest("Metro", null, null, null));

    assertThat(created.getName()).isEqualTo("Metro");
    assertThat(created.getEmail()).isNull();
  }

  @Test
  @DisplayName("delete refusé quand des produits référencent le fournisseur")
  void deleteSupplier_409QuandProduitsAssocies() {
    Supplier supplier = new Supplier("Metro");
    when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
    when(productRepository.existsBySupplierId(1L)).thenReturn(true);

    assertThatThrownBy(() -> supplierService.deleteSupplier(1L))
        .isInstanceOf(SupplierReferencedException.class);

    verify(supplierRepository, never()).delete(any());
  }

  @Test
  @DisplayName("update remplace les coordonnées optionnelles")
  void updateSupplier_metAJourLesChamps() {
    Supplier supplier = new Supplier("Metro");
    when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
    when(supplierRepository.save(any(Supplier.class))).thenAnswer(i -> i.getArgument(0));

    Supplier updated = supplierService.updateSupplier(
        1L, new SupplierUpsertRequest("Metro", "cmd@metro.fr", "0102030405", "Paris"));

    assertThat(updated.getEmail()).isEqualTo("cmd@metro.fr");
    assertThat(updated.getPhone()).isEqualTo("0102030405");
    assertThat(updated.getAddress()).isEqualTo("Paris");
  }

  @Test
  @DisplayName("getSupplierById lève SupplierNotFoundException si absent")
  void getSupplierById_notFound() {
    when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> supplierService.getSupplierById(99L))
        .isInstanceOf(SupplierNotFoundException.class);
  }
}
package com.restoforge.api.stock.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.restoforge.api.stock.dto.ProductUpsertRequest;
import com.restoforge.api.stock.supplier.Supplier;
import com.restoforge.api.stock.supplier.SupplierNotFoundException;
import com.restoforge.api.stock.supplier.SupplierRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private SupplierRepository supplierRepository;

  @InjectMocks
  private ProductService productService;

  @Test
  @DisplayName("getProductById renvoie le produit trouvé par le repository")
  void getProductByIdRenvoieLeProduitTrouve() {
    Product beurre = new Product("Beurre doux", "kg", new BigDecimal("8.40"),
        new Supplier("Metro"), new BigDecimal("6"));
    when(productRepository.findById(1L)).thenReturn(Optional.of(beurre));

    Product trouve = productService.getProductById(1L);

    assertThat(trouve).isSameAs(beurre);
  }

  @Test
  @DisplayName("getProductById lève ProductNotFoundException pour un id absent")
  void getProductByIdLeveUneExceptionPourUnIdAbsent() {
    when(productRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productService.getProductById(999L))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessageContaining("999");
  }

  @Test
  @DisplayName("getAllProducts belowThreshold délègue au repository")
  void getAllProducts_filtreSousSeuil() {
    when(productRepository.findAllBelowThreshold()).thenReturn(List.of());

    productService.getAllProducts(true);

    verify(productRepository).findAllBelowThreshold();
  }

  @Test
  @DisplayName("create lève SupplierNotFoundException si fournisseur inconnu")
  void createProduct_fournisseurInconnu() {
    ProductUpsertRequest request = new ProductUpsertRequest(
        "Farine", "kg", new BigDecimal("1"), 99L, new BigDecimal("5"), BigDecimal.ZERO);
    when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productService.createProduct(request))
        .isInstanceOf(SupplierNotFoundException.class);
  }

  @Test
  @DisplayName("delete lève ProductReferencedException si lignes de commande")
  void deleteProduct_referencee() {
    Product product = new Product("Farine", "kg", new BigDecimal("1"),
        new Supplier("Metro"), new BigDecimal("5"));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(productRepository.existsOrderLineForProduct(1L)).thenReturn(true);

    assertThatThrownBy(() -> productService.deleteProduct(1L))
        .isInstanceOf(ProductReferencedException.class);
  }
}
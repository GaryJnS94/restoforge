package com.restoforge.api.stock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test unitaire de ProductService, sans contexte Spring ni base de données.
 *
 * <p>
 * Le repository est un mock Mockito : on lui dicte ses réponses et on vérifie
 * uniquement la logique du service. Rapide, et indépendant de Docker.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

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
}

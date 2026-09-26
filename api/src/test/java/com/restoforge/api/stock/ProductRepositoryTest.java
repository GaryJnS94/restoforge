package com.restoforge.api.stock;

import static org.assertj.core.api.Assertions.assertThat;

import com.restoforge.api.TestcontainersConfiguration;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

/**
 * Test d'intégration de ProductRepository contre un PostgreSQL réel.
 *
 * <p>
 * {@code @DataJpaTest} ne démarre que la couche JPA (entités, repositories,
 * Flyway), pas les controllers ni les services. Chaque test s'exécute dans une
 * transaction annulée à la fin : les tests ne se polluent pas entre eux.
 *
 * <p>
 * Aucun profil n'est actif : la base part vide (pas de seed V100), chaque test
 * crée donc ses propres données.
 */
@DataJpaTest
// Par défaut, @DataJpaTest remplace la DataSource par une base embarquée en
// mémoire. NONE conserve le PostgreSQL fourni par Testcontainers.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  /**
   * Accès direct à JPA, réservé aux tests : il permet d'insérer un fournisseur
   * sans créer de SupplierRepository pour l'occasion.
   */
  @Autowired
  private TestEntityManager entityManager;

  private Supplier metro;

  @BeforeEach
  void creerUnFournisseur() {
    // product.supplier_id est NOT NULL : un produit exige un fournisseur en base.
    metro = entityManager.persist(new Supplier("Metro"));
  }

  @Test
  @DisplayName("findAll renvoie tous les produits enregistrés")
  void findAllRenvoieLesProduitsEnregistres() {
    productRepository.save(produit("Farine T55", "0.95", "20"));
    productRepository.save(produit("Beurre doux", "8.40", "6"));
    viderLeCache();

    List<Product> produits = productRepository.findAll();

    assertThat(produits)
        .extracting(Product::getName)
        .containsExactlyInAnyOrder("Farine T55", "Beurre doux");
  }

  @Test
  @DisplayName("findById relit un produit identique à celui enregistré")
  void findByIdRelitLeProduitEnregistre() {
    Long id = productRepository.save(produit("Beurre doux", "8.40", "6")).getId();
    viderLeCache();

    Product relu = productRepository.findById(id).orElseThrow();

    assertThat(relu.getName()).isEqualTo("Beurre doux");
    assertThat(relu.getUnit()).isEqualTo("kg");
    // BigDecimal : comparaison par valeur (compareTo), jamais equals —
    // 8.4 et 8.40 ne sont pas equals.
    assertThat(relu.getUnitPrice()).isEqualByComparingTo("8.40");
    assertThat(relu.getAlertThreshold()).isEqualByComparingTo("6");
    // Valeur initialisée en Java par le constructeur métier.
    assertThat(relu.getStock()).isEqualByComparingTo("0");
    assertThat(relu.getSupplier().getId()).isEqualTo(metro.getId());
  }

  @Test
  @DisplayName("findById renvoie un Optional vide pour un id inconnu")
  void findByIdRenvoieVidePourUnIdInconnu() {
    assertThat(productRepository.findById(999_999L)).isEmpty();
  }

  /**
   * Écrit les INSERT en attente puis vide le cache de premier niveau
   * d'Hibernate. Sans cela, findById renverrait l'objet encore en mémoire, sans
   * interroger la base : le test passerait sans rien prouver.
   */
  private void viderLeCache() {
    entityManager.flush();
    entityManager.clear();
  }

  private Product produit(String nom, String prix, String seuil) {
    return new Product(nom, "kg", new BigDecimal(prix), metro, new BigDecimal(seuil));
  }
}

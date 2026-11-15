package com.restoforge.api.stock.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import com.restoforge.api.TestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class SupplierRepositoryTest {

  @Autowired
  private SupplierRepository supplierRepository;

  @Test
  @DisplayName("save et findById relisent le fournisseur")
  void saveEtFindById() {
    Supplier saved = supplierRepository.save(new Supplier("Metro"));

    Supplier relu = supplierRepository.findById(saved.getId()).orElseThrow();

    assertThat(relu.getName()).isEqualTo("Metro");
  }
}
package com.restoforge.api.stock;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test de la couche web : seuls ProductController et GlobalExceptionHandler sont chargés.
 * Le service est remplacé par un mock ; aucune base n'est démarrée.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;                   // simule les requêtes HTTP, sans serveur

    @MockitoBean
    ProductService productService;     // remplace le vrai service dans le contexte Spring

    @Test
    void getAllProducts_returns200WithSupplierName() throws Exception {
        Supplier supplier = new Supplier("Minoterie Dupont");
        Product product = new Product("Farine T55", "kg", new BigDecimal("1.20"),
                supplier, new BigDecimal("10")); // stock 0 < seuil 10
        when(productService.getAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Farine T55"))
                .andExpect(jsonPath("$[0].supplierName").value("Minoterie Dupont"))
                .andExpect(jsonPath("$[0].belowThreshold").value(true));
    }

    @Test
    void getProductById_unknownId_returns404ProblemDetail() throws Exception {
        when(productService.getProductById(999L))
                .thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404));
    }
}
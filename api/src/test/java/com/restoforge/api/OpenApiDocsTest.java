package com.restoforge.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Vérifie la spec OpenAPI sur le contexte complet (springdoc + tous les controllers).
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocsTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  void apiDocs_exposesCatalogAndStockPaths() throws Exception {
    mockMvc.perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.paths['/products']").exists())
        .andExpect(jsonPath("$.paths['/products/{id}']").exists())
        .andExpect(jsonPath("$.paths['/products'].get.parameters[?(@.name=='belowThreshold')]").exists())
        .andExpect(jsonPath("$.paths['/suppliers']").exists())
        .andExpect(jsonPath("$.paths['/suppliers/{id}']").exists())
        .andExpect(jsonPath("$.paths['/products'].post").exists())
        .andExpect(jsonPath("$.paths['/products/{id}'].put").exists())
        .andExpect(jsonPath("$.paths['/products/{id}'].delete").exists())
        .andExpect(jsonPath("$.paths['/suppliers'].post").exists())
        .andExpect(jsonPath("$.paths['/suppliers/{id}'].put").exists())
        .andExpect(jsonPath("$.paths['/suppliers/{id}'].delete").exists());
  }
}